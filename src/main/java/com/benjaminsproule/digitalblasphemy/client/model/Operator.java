package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Operator {
    @JsonProperty("=")
    Equal("="),
    @JsonProperty(">")
    GreaterThan(">"),
    @JsonProperty(">=")
    GreaterThanOrEqual(">="),
    @JsonProperty("<")
    LessThan("<"),
    @JsonProperty("<=")
    LessThanOrEqual("<=");

    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }
}
