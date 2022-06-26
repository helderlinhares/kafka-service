package me.hl.kafkaservice.infra.utils;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import me.hl.kafkaservice.profiles.Profiles;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@ActiveProfiles(Profiles.TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmbeddedProducerUtils<T, K> {

    @Value("${spring.kafka.custom-properties.producer.schema-registry-url}")
    private String schemaRegistryUrl;

    public Producer<T, K> getProducer(EmbeddedKafkaBroker embeddedKafka, String topic) {

        var configProps = KafkaTestUtils.consumerProps(
                String.format("kafka-tests-%s", UUID.randomUUID()), "false", embeddedKafka);
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        var factory = new DefaultKafkaProducerFactory<T, K>(configProps);

        return factory.createProducer();
    }
}
