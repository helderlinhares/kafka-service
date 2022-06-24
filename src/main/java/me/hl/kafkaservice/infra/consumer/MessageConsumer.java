package me.hl.kafkaservice.infra.consumer;

import lombok.extern.slf4j.Slf4j;
import me.hl.kafkaservice.infra.config.consumer.MessageConsumerConfig;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumer {
    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
            containerFactory = MessageConsumerConfig.CONSUMER_MESSAGE_CREATED_BEAN_NAME)
    public void listenMessageCreated(ConsumerRecord<String, MessageCreatedEvent> message) {
        log.info("Received Message: " + message);
    }
}
