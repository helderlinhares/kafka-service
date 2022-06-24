package me.hl.kafkaservice.infra.config.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Service
@Slf4j
public class MessageDltProducerConfig extends AbstractProducerConfig<Object, Object> {

    public static final String PRODUCER_OBJECT_TEMPLATE_BEAN_NAME = "PRODUCER_OBJECT_TEMPLATE_BEAN";

    @Override
    public Map<String, Object> getCustomConfigProperties() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return configProps;
    }

    @Bean(PRODUCER_OBJECT_TEMPLATE_BEAN_NAME)
    public KafkaTemplate<Object, Object> dltKafkaTemplate() {
        return super.kafkaTemplate();
    }

}
