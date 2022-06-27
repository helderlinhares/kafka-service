package me.hl.kafkaservice.rest;

import me.hl.kafkaservice.infra.producer.MessageProducer;
import me.hl.kafkaservice.infra.producer.PoisonPillProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/kafka")
public record KafkaController(
        MessageProducer messageProducer,
        PoisonPillProducer poisonPillProducer
        ) {

    @PostMapping("publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void sendMessageToKafkaTopic(@Valid @RequestBody Request request){
        messageProducer.send(request.key(), request.message());
    }

    @PostMapping(value = "poison", consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void sendPoisonToKafkaTopic(@RequestBody String request){
        poisonPillProducer.send(request);
    }
}
