package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetAccountInformationResponse(
        @NonNull @JsonProperty(value = "db_core", required = true) DBCore dbCore,
        @NonNull @JsonProperty(required = true) User user
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(
            @JsonProperty(required = true) boolean active,
            @NonNull @JsonProperty(value = "display_name", required = true) String displayName,
            @JsonProperty(required = true) long id,
            @JsonProperty(required = true) boolean lifetime,
            @JsonProperty(required = true) boolean plus
    ) {
    }
}
