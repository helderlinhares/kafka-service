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

    abstract public T getKey();

    abstract public K getEvent();

    abstract public KafkaTemplate<T, K> getKafkaTemplate();

    public void send() {
        ListenableFuture<SendResult<T, K>> future =
                getKafkaTemplate().send(getTopic(), getKey(), getEvent());

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<T, K> result) {
                log.info("Sent message with key: '%s', value: '%s', from topic: '%s', partition '%s', and offset='%s'"
                        .formatted(getKey(),
                                getEvent(),
                                getTopic(),
                                result.getRecordMetadata().offset(),
                                result.getRecordMetadata().partition()));
            }

            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("Unable to send message with key: '%s', value: '%s', from topic: '%s' due to exception: '%s'"
                        .formatted(getKey(),
                                getEvent(),
                                getTopic(),
                                ex.getMessage()));
            }
        });
    }

}
