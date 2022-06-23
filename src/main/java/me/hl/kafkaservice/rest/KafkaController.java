package me.hl.kafkaservice.rest;

import me.hl.kafkaservice.infra.MessageProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("kafka")
public record KafkaController(MessageProducer messageProducer) {
    @PostMapping("publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void sendMessageToKafkaTopic(@Valid @RequestBody MessageRequest messageRequest){
        messageProducer.send(messageRequest);
    }
}
