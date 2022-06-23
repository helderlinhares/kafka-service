package me.hl.kafkaservice.infra.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.AllArgsConstructor;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@EnableKafka
@Configuration
@AllArgsConstructor
public class KafkaMessageConsumerConfig {
    public static final String MESSAGE_CREATED_BEAN_NAME = "MESSAGE_CREATED_BEAN";
    private KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, MessageCreatedEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializer());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getValueDeserializer());

        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getAutoCommit());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getOffsetReset());

        configProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, kafkaProperties.getSpecificAvroReader());
        configProps.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSchemaRegistryUrl());

        return new DefaultKafkaConsumerFactory<>(configProps);
    }
    @Bean
    DefaultErrorHandler dlqErrorHandler() {
        return new DefaultErrorHandler((rec, ex) -> {
            System.out.println("Recovered: " + rec);
        }, new FixedBackOff(0L, 0L));
    }

    @Bean(MESSAGE_CREATED_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(dlqErrorHandler());
        return factory;
    }
}
