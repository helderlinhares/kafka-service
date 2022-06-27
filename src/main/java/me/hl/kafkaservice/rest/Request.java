package me.hl.kafkaservice.rest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public record Request(@NotBlank String key, @Valid MessageRequest message) {}