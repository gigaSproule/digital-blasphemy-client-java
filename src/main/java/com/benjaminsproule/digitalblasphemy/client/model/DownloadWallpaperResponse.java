package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DownloadWallpaperResponse(
        @NonNull @JsonProperty(value = "db_core", required = true) DBCore dbCore,
        @NonNull @JsonProperty(required = true) Download download) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            @JsonProperty(required = true)
            long timestamp,
            @NonNull @JsonProperty(required = true) Endpoints endpoints,
            @NonNull @JsonProperty(required = true) Request request) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(@NonNull @JsonProperty(required = true) Params params) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Params(
                    @NonNull @JsonProperty(required = true) WallpaperType type,
                    @JsonProperty(required = true) int width,
                    @JsonProperty(required = true) int height,
                    @JsonProperty(value = "wallpaper_id", required = true) int wallpaperId) {
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Download(
            @JsonProperty(required = true) long expiration,
            @NonNull @JsonProperty(required = true) String url) {
    }
}
