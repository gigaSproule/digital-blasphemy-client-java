package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Endpoints(
        @NonNull String api,
        @NonNull String image,
        @NonNull String thumb,
        @NonNull String web
) {
}
