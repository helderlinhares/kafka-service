package me.hl.kafkaservice.infra;

import me.hl.kafkaservice.infra.consumer.MessageConsumer;
import me.hl.kafkaservice.infra.utils.EmbeddedProducerUtils;
import me.hl.message.MessageContent;
import me.hl.message.MessageCreatedEvent;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka(
        topics = {"${spring.kafka.template.default-topic}"},
        partitions = 1
)
class MessageConsumerTest extends EmbeddedProducerUtils<String, MessageCreatedEvent> {

    private static final int TIMEOUT_IN_MILLISECONDS = 5000;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private Producer<String, MessageCreatedEvent> producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @SpyBean
    private MessageConsumer messageConsumer;

    @Captor
    ArgumentCaptor<MessageCreatedEvent> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<String> topicArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> partitionArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> offsetArgumentCaptor;

    @BeforeAll
    private void setupKafka() {
        producer = getProducer(embeddedKafkaBroker, topic);
    }

    @AfterAll
    private void tearDown(){
        producer.close();
    }

    @Test
    void shouldConsumeMessage() {
        var message = buildMessage();
        producer.send(new ProducerRecord<>(topic, 0, "", message));
        producer.flush();

        Mockito.verify(messageConsumer, Mockito.timeout(TIMEOUT_IN_MILLISECONDS))
                .listenMessageCreated(messageArgumentCaptor.capture(), topicArgumentCaptor.capture(),
                partitionArgumentCaptor.capture(), offsetArgumentCaptor.capture());

        MessageCreatedEvent messageConsumed = messageArgumentCaptor.getValue();

        assertNotNull(messageConsumed);
        assertEquals(messageConsumed.getCode(), message.getCode());
        assertEquals(messageConsumed.getContent().getTitle(), message.getContent().getTitle());
        assertEquals(messageConsumed.getContent().getBody(), message.getContent().getBody());
        assertEquals(topic, topicArgumentCaptor.getValue());
        assertEquals(0, partitionArgumentCaptor.getValue());
        assertEquals(0, offsetArgumentCaptor.getValue());
    }

    private MessageCreatedEvent buildMessage(){
        var messageCode = UUID.randomUUID().toString();

        return new MessageCreatedEvent(
                messageCode,
                new MessageContent(
                        String.format("Title %s", messageCode),
                        String.format("Body %s", messageCode)
                ),
                LocalDateTime.now().toString()
        );
    }
}