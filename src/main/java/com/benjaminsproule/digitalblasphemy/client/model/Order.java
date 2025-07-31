package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum Order {
    @JsonProperty("asc")
    ASCENDING("asc"),
    @JsonProperty("desc")
    DESCENDING("desc");

    private final String order;

    Order(String order) {
        this.order = order;
    }

    public String toString() {
        return this.order;
    }

    public static Order of(String order) {
        return Arrays.stream(Order.values())
                .filter(value -> value.order.equals(order))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s is not a valid Order".formatted(order)));
    }
}
