package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum WallpaperType {
    @JsonProperty("single")
    SINGLE("single"),
    @JsonProperty("dual")
    DUAL("dual"),
    @JsonProperty("triple")
    TRIPLE("triple"),
    @JsonProperty("mobile")
    MOBILE("mobile");

    private final String type;

    WallpaperType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }

    public static WallpaperType of(String type) {
        return Arrays.stream(WallpaperType.values())
                .filter(value -> value.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s is not a valid WallpaperType".formatted(type)));
    }
}
