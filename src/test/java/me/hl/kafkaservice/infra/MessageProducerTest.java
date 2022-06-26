package me.hl.kafkaservice.infra;

import me.hl.kafkaservice.infra.utils.EmbeddedConsumerUtils;
import me.hl.kafkaservice.rest.ContentRequest;
import me.hl.kafkaservice.rest.MessageRequest;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import me.hl.kafkaservice.infra.producer.MessageProducer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka(
        topics = {"${spring.kafka.template.default-topic}"},
        partitions = 1
)
class MessageProducerTest extends EmbeddedConsumerUtils<String, MessageCreatedEvent> {

    private static final int TIMEOUT_IN_MILLISECONDS = 5000;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private Consumer<String, MessageCreatedEvent> consumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MessageProducer messageProducer;

    @BeforeAll
    private void setupKafka() {
        consumer = getConsumer(embeddedKafkaBroker, topic);
    }

    @AfterAll
    private void tearDown(){
        consumer.close();
    }

    @Test
    void shouldProduceMessage(){
        var message = buildMessageRequest();
        messageProducer.send(message);

        var event = KafkaTestUtils.getSingleRecord(consumer, topic, TIMEOUT_IN_MILLISECONDS);

        assertNotNull(event);
        assertEquals(event.value().getCode(), message.code());
        assertEquals(event.value().getContent().getTitle(), message.content().title());
        assertEquals(event.value().getContent().getBody(), message.content().body());
    }

    private MessageRequest buildMessageRequest(){
        var messageCode = UUID.randomUUID().toString();

        return new MessageRequest(
                messageCode,
                new ContentRequest(
                        String.format("Title %s", messageCode),
                        String.format("Body %s", messageCode)
                )
        );
    }
}