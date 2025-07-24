package com.benjaminsproule.digitalblasphemy.client.model;

public enum WallpaperType {
    Single("single"),
    Dual("dual"),
    Triple("triple"),
    Mobile("mobile");

    private final String type;

    WallpaperType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }
}
