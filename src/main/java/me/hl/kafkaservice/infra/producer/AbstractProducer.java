package me.hl.kafkaservice.infra.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
abstract public class AbstractProducer<T, K> {

    abstract public String getTopic();

    abstract public K getEvent();

    public void send(KafkaTemplate<T, K> kafkaTemplate){
        ListenableFuture<SendResult<T, K>> future =
                kafkaTemplate.send(getTopic(), getEvent());

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<T, K> result) {
                log.info("Sent message=[" + getEvent() + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("Unable to send message=[" + getEvent() + "] due to : " + ex.getMessage());
            }
        });
    }

}
