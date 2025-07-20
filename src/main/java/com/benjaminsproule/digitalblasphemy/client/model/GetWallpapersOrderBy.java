package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
