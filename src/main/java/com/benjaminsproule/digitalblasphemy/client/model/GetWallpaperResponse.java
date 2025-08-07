package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetWallpaperResponse(
        @NonNull @JsonProperty(value = "db_core", required = true) DBCore dbCore,
        @Nullable Wallpaper wallpaper
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            @JsonProperty(required = true) long timestamp,
            @NonNull @JsonProperty(required = true) Endpoints endpoints,
            @NonNull @JsonProperty(required = true) Request request
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(
                @NonNull @JsonProperty(required = true) Params params,
                @NonNull @JsonProperty(required = true) Query query
        ) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Params(@JsonProperty(value = "wallpaper_id", required = true) long wallpaperId) {
            }

            public record Query(
                    @JsonProperty(value = "filter_res_height", required = true) long filterResHeight,
                    @NonNull @JsonProperty(value = "filter_res_operator", required = true) Operator filterResOperator,
                    @Nullable @JsonProperty(value = "filter_res_operator_height") Operator filterResOperatorHeight,
                    @Nullable @JsonProperty("filter_res_operator_width") Operator filterResOperatorWidth,
                    @JsonProperty(value = "filter_res_width", required = true) long filterResWidth,
                    @JsonProperty(value = "show_comments", required = true) boolean showComments,
                    @JsonProperty(value = "show_pickle_jar", required = true) boolean showPickleJar,
                    @JsonProperty(value = "show_resolutions", required = true) boolean showResolutions
            ) {
            }
        }
    }
}
