package me.hl.kafkaservice.infra;

import lombok.extern.slf4j.Slf4j;
import me.hl.kafkaservice.rest.MessageRequest;
import me.hl.message.MessageContent;
import me.hl.message.MessageCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MessageProducer {
    @Value("${kafka.topic.message}")
    private String topic;

    private final KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(MessageRequest message) {
        var messageCreatedEvent = MessageCreatedEvent.newBuilder()
                .setCode(message.code())
                .setContent(
                        MessageContent.newBuilder()
                                .setTitle(message.content().title())
                                .setBody(message.content().body())
                                .build()
                )
                .setCreatedAt(String.valueOf(LocalDateTime.now()))
                .build();

        ListenableFuture<SendResult<String, MessageCreatedEvent>> future =
                kafkaTemplate.send(topic, messageCreatedEvent);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, MessageCreatedEvent> result) {
                log.info("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }

}
