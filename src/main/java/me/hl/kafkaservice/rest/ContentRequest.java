package me.hl.kafkaservice.rest;

import javax.validation.constraints.NotBlank;

public record ContentRequest(@NotBlank String title, @NotBlank String body){}
