package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record Wallpaper(
        @JsonProperty(required = true)
        int id,
        @Nullable
        Boolean all_free,
        @Nullable
        Comments comments,
        @Nullable
        String content,
        @Nullable
        Boolean free,
        @NonNull
        @JsonProperty(required = true)
        String name,
        @NonNull
        @JsonProperty(required = true)
        Paths paths,
        @Nullable
        PickleJar pickle_jar,
        @Nullable
        String rating, // Double
        @Nullable
        Resolutions resolutions,
        @Nullable
        String sku,
        @Nullable
        Map<String, Tag> tags,
        @Nullable
        Long timestamp) {

    public record Comments(List<Comment> comments) {
        public record Comment(
                @NonNull
                @JsonProperty(required = true)
                String id,
                @NonNull
                @JsonProperty(required = true)
                String author_id,
                @NonNull
                @JsonProperty(required = true)
                String author_display,
                @NonNull
                @JsonProperty(required = true)
                String content,
                @NonNull
                @JsonProperty(required = true)
                String rating,
                long timestamp
        ) {
        }
    }

    public record Paths(
            @NonNull
            @JsonProperty(required = true)
            String api,
            @NonNull
            @JsonProperty(required = true)
            String thumb,
            @NonNull
            @JsonProperty(required = true)
            String web
    ) {
    }

    public record PickleJar(
            @NonNull
            @JsonProperty(required = true)
            String parent,
            @NonNull
            @JsonProperty(required = true)
            List<String> siblings
    ) {
    }

    public record Resolutions(
            @NonNull
            @JsonProperty(required = true)
            List<Resolution> single,
            @Nullable
            List<Resolution> dual,
            @Nullable
            List<Resolution> triple,
            @Nullable
            List<Resolution> mobile
    ) {
        public record Resolution(
                @NonNull
                @JsonProperty(required = true)
                String label,
                @NonNull
                @JsonProperty(required = true)
                String width,
                @NonNull
                @JsonProperty(required = true)
                String height,
                @NonNull
                @JsonProperty(required = true)
                String image
        ) {
        }
    }

    public record Tag(
            @JsonProperty(required = true)
            long id,
            @NonNull
            @JsonProperty(required = true)
            String name
    ) {
    }
}
