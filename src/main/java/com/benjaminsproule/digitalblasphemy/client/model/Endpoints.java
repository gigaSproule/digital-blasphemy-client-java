package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Endpoints(
        @NotNull String api,
        @NotNull String image,
        @NotNull String thumb,
        @NotNull String web
) {
}
