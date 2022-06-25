package me.hl.kafkaservice.infra.producer;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import me.hl.kafkaservice.profiles.Profiles;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles(Profiles.TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        topics = {"${spring.kafka.template.default-topic}"}
)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProducerTestUtils<T, K> {

    public Consumer<T, K> getConsumer(EmbeddedKafkaBroker embeddedKafka, String topic) {

        var configProps = KafkaTestUtils.consumerProps(
                String.format("kafka-tests-%s", UUID.randomUUID()), "false", embeddedKafka);
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://schema-registry");
        configProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        var factory = new DefaultKafkaConsumerFactory<T, K>(configProps);
        var consumer = factory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topic);

        return consumer;
    }
}
