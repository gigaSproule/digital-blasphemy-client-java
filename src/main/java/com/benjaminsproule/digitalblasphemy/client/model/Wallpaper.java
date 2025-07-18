package com.benjaminsproule.digitalblasphemy.client.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
        @NonNull
        String name,
        @NonNull
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
                String id,
                @NonNull
                String author_id,
                @NonNull
                String author_display,
                @NonNull
                String content,
                @NonNull
                String rating,
                long timestamp
        ) {
        }
    }

    public record Paths(
            @NonNull
            String api,
            @NonNull
            String thumb,
            @NonNull
            String web
    ) {
    }

    public record PickleJar(
            @NonNull
            String parent,
            @NonNull
            List<String> siblings
    ) {
    }

    public record Resolutions(
            @NonNull
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
                String label,
                @NonNull
                String width,
                @NonNull
                String height,
                @NonNull
                String image
        ) {
        }
    }

    public record Tag(
            long id,
            @NonNull
            String name
    ) {
    }
}
