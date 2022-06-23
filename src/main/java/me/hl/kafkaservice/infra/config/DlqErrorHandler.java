package me.hl.kafkaservice.infra.config;

import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerAwareErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

//https://docs.spring.io/spring-kafka/docs/current/reference/html/#error-handling-deserializer
//https://stackoverflow.com/questions/70252047/this-error-handler-cannot-process-serializationexceptions-directly-please-con
@Component
public class DlqErrorHandler implements ContainerAwareErrorHandler {
    private final KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate;

    public DlqErrorHandler(KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {

        ConsumerRecord<String, MessageCreatedEvent> record = (ConsumerRecord<String, MessageCreatedEvent>) records.get(0);
        try {
            kafkaTemplate.send("dlqTopic", record.key(), record.value());
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset() + 1);
            // Other records may be from other partitions, so seek to current offset for other partitions too
            // ...
        } catch (Exception e) {
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
            // Other records may be from other partitions, so seek to current offset for other partitions too
            // ...
            throw new KafkaException("Seek to current after exception", thrownException);
        }

    }
}
