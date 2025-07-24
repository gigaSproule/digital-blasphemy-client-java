package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WallpaperType {
    @JsonProperty("single")
    Single("single"),
    @JsonProperty("dual")
    Dual("dual"),
    @JsonProperty("triple")
    Triple("triple"),
    @JsonProperty("mobile")
    Mobile("mobile");

    private final String type;

    WallpaperType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }
}
