package me.hl.kafkaservice.rest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String senderName, @Valid ContentRequest content) {}
