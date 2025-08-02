package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Endpoints(
        @NonNull @JsonProperty(required = true) String api,
        @NonNull @JsonProperty(required = true) String image,
        @NonNull @JsonProperty(required = true) String thumb,
        @NonNull @JsonProperty(required = true) String web
) {
}
