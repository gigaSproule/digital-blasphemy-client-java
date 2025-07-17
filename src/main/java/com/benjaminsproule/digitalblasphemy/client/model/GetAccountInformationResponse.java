package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetAccountInformationResponse(
        @NotNull @JsonProperty("db_core") DBCore dbCore,
        @NotNull User user
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(
            boolean active,
            @JsonProperty("display_name") String displayName,
            long id,
            boolean lifetime,
            boolean plus
    ) {
    }
}
