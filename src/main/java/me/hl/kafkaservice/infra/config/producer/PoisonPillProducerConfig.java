package me.hl.kafkaservice.infra.config.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Service
public class PoisonPillProducerConfig extends AbstractProducerConfig<Object, Object> {

    public static final String PRODUCER_POISON_PILL_TEMPLATE_BEAN_NAME = "PRODUCER_POISON_PILL_TEMPLATE_BEAN";

    @Value("${spring.kafka.custom-properties.producer.schema-registry-url}")
    private String schemaRegistryUrl;

    @Value("${spring.kafka.producer.acks}")
    private String acksConfig;

    @Value("${spring.kafka.producer.retries}")
    private String retriesConfig;

    @Override
    public Map<String, Object> getCustomConfigProperties() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ACKS_CONFIG, acksConfig);
        configProps.put(RETRIES_CONFIG, retriesConfig);
        configProps.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return configProps;
    }

    @Bean(PRODUCER_POISON_PILL_TEMPLATE_BEAN_NAME)
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return super.kafkaTemplate();
    }

}
