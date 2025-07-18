package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetWallpaperResponse(
        @NonNull @JsonProperty("db_core") DBCore dbCore,
        @Nullable Wallpaper wallpaper
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp,
            @NonNull Endpoints endpoints,
            @NonNull Request request
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(
                @NonNull Params params,
                @NonNull Query query
        ) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Params(long wallpaperId) {
            }

            public record Query(
                    @JsonProperty("filter_res_height") long filterResHeight,
                    @JsonProperty("filter_res_operator") @NonNull Operator filterResOperator,
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
