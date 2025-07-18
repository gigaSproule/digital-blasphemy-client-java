package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetAccountInformationResponse(
        @NonNull @JsonProperty("db_core") DBCore dbCore,
        @NonNull User user
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
