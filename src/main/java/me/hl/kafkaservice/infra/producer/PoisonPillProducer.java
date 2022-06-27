package me.hl.kafkaservice.infra.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static me.hl.kafkaservice.infra.config.producer.PoisonPillProducerConfig.PRODUCER_POISON_PILL_TEMPLATE_BEAN_NAME;

@Service
public class PoisonPillProducer extends AbstractProducer<Object, Object>{

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private Object message;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public PoisonPillProducer(@Qualifier(PRODUCER_POISON_PILL_TEMPLATE_BEAN_NAME) KafkaTemplate<Object, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public Object getKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Object getEvent() {
        return message;
    }

    @Override
    public KafkaTemplate<Object, Object> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public void send(Object message){
        this.message = message;
        super.send();
    }
}
