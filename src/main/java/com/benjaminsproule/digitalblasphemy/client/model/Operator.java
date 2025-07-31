package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum Operator {
    @JsonProperty("=")
    EQUAL("="),
    @JsonProperty(">")
    GREATER_THAN(">"),
    @JsonProperty(">=")
    GREATER_THAN_OR_EQUAL(">="),
    @JsonProperty("<")
    LESS_THAN("<"),
    @JsonProperty("<=")
    LESS_THAN_OR_EQUAL("<=");

    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public String toString() {
        return this.operator;
    }

    public static Operator of(String operator) {
        return Arrays.stream(Operator.values())
                .filter(value -> value.operator.equals(operator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s is not a valid Operator".formatted(operator)));
    }
}
