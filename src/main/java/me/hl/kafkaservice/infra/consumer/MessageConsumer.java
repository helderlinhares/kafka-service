package me.hl.kafkaservice.infra.consumer;

import lombok.extern.slf4j.Slf4j;
import me.hl.kafkaservice.infra.config.consumer.MessageConsumerConfig;
import me.hl.message.MessageCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumer {
    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
            containerFactory = MessageConsumerConfig.CONSUMER_MESSAGE_CREATED_BEAN_NAME)
    public void listenMessageCreated(@Payload MessageCreatedEvent message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                                     @Header(KafkaHeaders.OFFSET) Long offset) {
        log.info("Received a message with code: '{}', from topic: '{}', partition: '{}', and offset: '{}'",
                message.getCode(), topic, partition, offset);
    }
}
