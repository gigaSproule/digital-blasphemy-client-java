package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DownloadWallpaperResponse(
        @NonNull @JsonProperty("db_core") DBCore dbCore,
        @NonNull Download download) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp,
            @NonNull Endpoints endpoints,
            @NonNull Request request) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(@NonNull Params params) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Params(
                    @NonNull WallpaperType type,
                    int width,
                    int height,
                    @JsonProperty("wallpaper_id") int wallpaperId) {
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Download(
            long expiration,
            @NonNull String url) {
    }
}
