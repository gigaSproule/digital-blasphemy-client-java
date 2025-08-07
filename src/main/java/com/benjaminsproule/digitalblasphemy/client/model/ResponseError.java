package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseError(
        @JsonProperty(required = true) int code,
        @NonNull @JsonProperty(required = true) String description,
        @Nullable List<String> errors
) {
}
