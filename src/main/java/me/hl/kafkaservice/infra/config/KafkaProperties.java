package me.hl.kafkaservice.infra.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
@NoArgsConstructor
public class KafkaProperties {
    //Common
    private List<String> bootstrapServers;
    private String schemaRegistryUrl;
    //Producer
    private String acksConfig;
    private String retriesConfig;
    private Class<?> keySerializer = StringSerializer.class;
    private Class<?> valueSerializer = KafkaAvroSerializer.class;
    //Consumer
    private Boolean autoCommit;
    private String groupId;
    private String offsetReset;
    private Boolean specificAvroReader;
    private Class<?> keyDeserializer = StringDeserializer.class;
    private Class<?> valueDeserializer = KafkaAvroDeserializer.class;
}
