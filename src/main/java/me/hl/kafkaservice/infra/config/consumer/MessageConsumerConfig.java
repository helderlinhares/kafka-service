package me.hl.kafkaservice.infra.config.consumer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

import static me.hl.kafkaservice.infra.config.producer.MessageDltProducerConfig.PRODUCER_OBJECT_TEMPLATE_BEAN_NAME;

@Configuration
@EnableKafka
@Slf4j
public class MessageConsumerConfig extends AbstractConsumerConfig {
    public static final String CONSUMER_MESSAGE_CREATED_BEAN_NAME = "CONSUMER_MESSAGE_CREATED_BEAN";

    @Value("${spring.kafka.dlt.enable}")
    private Boolean enableDlt;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public MessageConsumerConfig(@Qualifier(PRODUCER_OBJECT_TEMPLATE_BEAN_NAME) KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Map<String, Object> getCustomConfigProperties() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);

        return configProps;
    }

    @ConditionalOnExpression("${enableDlt}")
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        return new DefaultErrorHandler(deadLetterPublishingRecoverer);
    }

    @ConditionalOnExpression("${enableDlt}")
    public DeadLetterPublishingRecoverer publisher(KafkaTemplate<Object, Object> bytesTemplate) {
        return new DeadLetterPublishingRecoverer(bytesTemplate);
    }

    @Bean(CONSUMER_MESSAGE_CREATED_BEAN_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        if (enableDlt){
            factory.setCommonErrorHandler(errorHandler(publisher(kafkaTemplate)));
        }else{
            factory.setCommonErrorHandler(new SkipMessageErrorHandler());
        }
        return factory;
    }

}
