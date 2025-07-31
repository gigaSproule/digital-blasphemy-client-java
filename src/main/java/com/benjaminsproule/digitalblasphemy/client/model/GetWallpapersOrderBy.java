package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum GetWallpapersOrderBy {
    @JsonProperty("date")
    DATE("date"),
    @JsonProperty("name")
    NAME("name");

    private final String orderBy;

    GetWallpapersOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String toString() {
        return this.orderBy;
    }

    public static GetWallpapersOrderBy of(String orderBy) {
        return Arrays.stream(GetWallpapersOrderBy.values())
                .filter(value -> value.orderBy.equals(orderBy))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s is not a valid GetWallpapersOrderBy".formatted(orderBy)));
    }
}
