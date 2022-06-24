package me.hl.kafkaservice.infra.config.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@Slf4j
abstract public class AbstractProducerConfig<T, K> {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    abstract public Map<String, Object> getCustomConfigProperties();

    private ProducerFactory<T, K> producerFactory() {
        Map<String, Object> configProps = new HashMap<>(getCustomConfigProperties());
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<T, K> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
