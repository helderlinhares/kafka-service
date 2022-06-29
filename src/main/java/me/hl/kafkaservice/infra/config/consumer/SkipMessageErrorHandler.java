package me.hl.kafkaservice.infra.config.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
public class SkipMessageErrorHandler implements CommonErrorHandler {

    @Override
    public void handleRemaining(@NonNull Exception thrownException,
                                List<ConsumerRecord<?, ?>> records,
                                @NonNull Consumer<?, ?> consumer,
                                @NonNull MessageListenerContainer container) {
        for (ConsumerRecord<?, ?> record : records) {
            handleRecord(thrownException, record, consumer, container);
        }
    }

    @Override
    @Transactional
    public void handleRecord(Exception thrownException,
                             ConsumerRecord<?, ?> record,
                             Consumer<?, ?> consumer,
                             @NonNull MessageListenerContainer container) {
        String topic = record.topic();
        var offset = record.offset();
        var partition = record.partition();
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        log.error("Handling when processing a kafka event with exception {}", thrownException.getMessage());
        consumer.seek(topicPartition, offset + 1);
        log.warn("Skipped event from topic: '{}', partition: '{}', and offset: '{}'", topic, partition, offset);
    }

}
