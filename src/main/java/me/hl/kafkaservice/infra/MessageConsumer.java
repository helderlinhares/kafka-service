package me.hl.kafkaservice.infra;

import lombok.extern.slf4j.Slf4j;
import me.hl.kafkaservice.infra.config.KafkaMessageConsumerConfig;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {
    @KafkaListener(topics = "${kafka.topic.message}",
            containerFactory = KafkaMessageConsumerConfig.MESSAGE_CREATED_BEAN_NAME)
    public void listenMessageCreated(ConsumerRecord<String, MessageCreatedEvent> message) {
        log.info("Received Message: " + message);
    }
}
