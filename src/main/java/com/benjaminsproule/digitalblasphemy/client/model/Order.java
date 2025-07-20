package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
