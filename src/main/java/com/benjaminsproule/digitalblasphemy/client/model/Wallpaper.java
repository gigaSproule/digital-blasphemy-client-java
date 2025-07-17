package com.benjaminsproule.digitalblasphemy.client.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record Wallpaper(
        int id,
        @Nullable
        Boolean all_free,
        @Nullable
        Comments comments,
        @Nullable
        String content,
        @Nullable
        Boolean free,
        @NotNull
        String name,
        @NotNull
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
                @NotNull
                String id,
                @NotNull
                String author_id,
                @NotNull
                String author_display,
                @NotNull
                String content,
                @NotNull
                String rating,
                long timestamp
        ) {
        }
    }

    public record Paths(
            @NotNull
            String api,
            @NotNull
            String thumb,
            @NotNull
            String web
    ) {
    }

    public record PickleJar(
            @NotNull
            String parent,
            @NotNull
            List<String> siblings
    ) {
    }

    public record Resolutions(
            @NotNull
            List<Resolution> single,
            @Nullable
            List<Resolution> dual,
            @Nullable
            List<Resolution> triple,
            @Nullable
            List<Resolution> mobile
    ) {
        public record Resolution(
                @NotNull
                String label,
                @NotNull
                String width,
                @NotNull
                String height,
                @NotNull
                String image
        ) {
        }
    }

    public record Tag(
            long id,
            @NotNull
            String name
    ) {
    }
}
