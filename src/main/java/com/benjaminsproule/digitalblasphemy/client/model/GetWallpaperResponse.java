package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetWallpaperResponse(
        @NotNull @JsonProperty("db_core") DBCore dbCore,
        @Nullable Wallpaper wallpaper
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp,
            @NotNull Endpoints endpoints,
            @NotNull Request request
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(
                @NotNull Params params,
                @NotNull Query query
        ) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Params(long wallpaperId) {
            }

            public record Query(
                    @JsonProperty("filter_res_height") long filterResHeight,
                    @JsonProperty("filter_res_operator") @NotNull Operator filterResOperator,
                    @JsonProperty("filter_res_operator_height") @Nullable Operator filterResOperatorHeight,
                    @JsonProperty("filter_res_operator_width") @Nullable Operator filterResOperatorWidth,
                    @JsonProperty("filter_res_width") long filterResWidth,
                    @JsonProperty("show_comments") boolean showComments,
                    @JsonProperty("show_pickle_jar") boolean showPickleJar,
                    @JsonProperty("show_resolutions") boolean showResolutions
            ) {
            }
        }
    }
}
