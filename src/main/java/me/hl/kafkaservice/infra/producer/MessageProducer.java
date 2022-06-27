package me.hl.kafkaservice.infra.producer;

import lombok.extern.slf4j.Slf4j;
import me.hl.kafkaservice.rest.MessageRequest;
import me.hl.message.MessageContent;
import me.hl.message.MessageCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static me.hl.kafkaservice.infra.config.producer.MessageProducerConfig.PRODUCER_MESSAGE_TEMPLATE_BEAN_NAME;

@Service
@Slf4j
public class MessageProducer extends AbstractProducer<String, MessageCreatedEvent>{

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private String key;

    private MessageRequest message;

    private final KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate;

    public MessageProducer(@Qualifier(PRODUCER_MESSAGE_TEMPLATE_BEAN_NAME) KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public MessageCreatedEvent getEvent() {
        return MessageCreatedEvent.newBuilder()
                .setSenderName(message.senderName())
                .setContent(
                        MessageContent.newBuilder()
                                .setTitle(message.content().title())
                                .setBody(message.content().body())
                                .build()
                )
                .setCreatedAt(String.valueOf(LocalDateTime.now()))
                .build();
    }

    @Override
    public KafkaTemplate<String, MessageCreatedEvent> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public void send(String key, MessageRequest message){
        this.key = key;
        this.message = message;
        super.send();
    }
}
