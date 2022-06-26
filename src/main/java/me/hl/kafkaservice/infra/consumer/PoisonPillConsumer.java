package me.hl.kafkaservice.infra.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static me.hl.kafkaservice.infra.config.consumer.MessageDltConsumerConfig.CONSUMER_OBJECT_BEAN_NAME;

@Service
@Slf4j
@ConditionalOnExpression("${spring.kafka.dlt.enable}")
public class PoisonPillConsumer {

    @KafkaListener(topics = {"${spring.kafka.template.default-topic}.DLT"}, containerFactory = CONSUMER_OBJECT_BEAN_NAME)
    public void recoverDLT(@Payload ConsumerRecord<String, byte[]> consumerRecord) {
        log.info("Poison pill consumed with value: '{}', from topic: '{}', partition: '{}', and offset: '{}'",
                new String(consumerRecord.value()),
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset());
    }
}
