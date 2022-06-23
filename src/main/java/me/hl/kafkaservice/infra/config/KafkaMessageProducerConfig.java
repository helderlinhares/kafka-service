package me.hl.kafkaservice.infra.config;

import lombok.AllArgsConstructor;
import me.hl.message.MessageCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@AllArgsConstructor
public class KafkaMessageProducerConfig {
    private KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, MessageCreatedEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ACKS_CONFIG, kafkaProperties.getAcksConfig());
        configProps.put(RETRIES_CONFIG, kafkaProperties.getRetriesConfig());

        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializer());
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializer());

        configProps.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSchemaRegistryUrl());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
