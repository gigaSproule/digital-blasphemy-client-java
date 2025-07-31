package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetWallpapersResponse(
        @NonNull @JsonProperty("db_core") DBCore dbCore,
        @NonNull List<Integer> wallpapers
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            long timestamp,
            @NonNull Endpoints endpoints,
            @NonNull Request request,
            @JsonProperty("total_pages") int totalPages,
            @NonNull Map<String, Wallpaper> wallpapers
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(@NonNull Query query) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Query(
                    @Nullable @JsonProperty("filter_date_day") Integer filterDateDay,
                    @Nullable @JsonProperty("filter_date_month") Integer filterDateMonth,
                    @Nullable @JsonProperty("filter_date_year") Integer filterDateYear,
                    @NonNull @JsonProperty("filter_date_operator") Operator filterDateOperator,
                    @Nullable @JsonProperty("filter_gallery") List<Integer> filterGallery,
                    @Nullable @JsonProperty("filter_rating") Integer filterRating,
                    @Nullable @JsonProperty("filter_rating_operator") Operator filterRatingOperator,
                    @Nullable @JsonProperty("filter_res_operator_height") Operator filterResOperatorHeight,
                    @Nullable @JsonProperty("filter_res_operator_width") Operator filterResOperatorWidth,
                    @JsonProperty("filter_res_height") int filterResHeight,
                    @NonNull @JsonProperty("filter_res_operator") Operator filterResOperator,
                    @JsonProperty("filter_res_width") int filterResWidth,
                    @Nullable @JsonProperty("filter_tag") List<Integer> filterTag,
                    int limit,
                    @NonNull Order order,
                    @NonNull @JsonProperty("order_by") GetWallpapersOrderBy orderBy,
                    int page,
                    @Nullable String s,
                    @JsonProperty("show_comments") boolean showComments,
                    @JsonProperty("show_pickle_jar") boolean showPickleJar,
                    @JsonProperty("show_resolutions") boolean showResolutions
            ) {
            }
        }
    }
}
