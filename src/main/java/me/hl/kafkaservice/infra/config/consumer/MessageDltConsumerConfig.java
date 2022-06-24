package me.hl.kafkaservice.infra.config.consumer;

import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnExpression("${spring.kafka.dlt.enable}")
public class MessageDltConsumerConfig extends AbstractConsumerConfig {

    public static final String CONSUMER_OBJECT_BEAN_NAME = "CONSUMER_OBJECT_BEAN";

    public Map<String, Object> getCustomConfigProperties() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

        return configProps;
    }

    @Bean(CONSUMER_OBJECT_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> kafkaDltListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new SkipMessageErrorHandler());
        return factory;
    }

}
