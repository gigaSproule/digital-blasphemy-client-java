package com.benjaminsproule.digitalblasphemy.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetWallpapersResponse(
        @NonNull @JsonProperty(value = "db_core", required = true) DBCore dbCore,
        @NonNull @JsonProperty(required = true) List<Integer> wallpapers
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DBCore(
            @JsonProperty(required = true) long timestamp,
            @NonNull @JsonProperty(required = true) Endpoints endpoints,
            @NonNull @JsonProperty(required = true) Request request,
            @JsonProperty(value = "total_pages", required = true) int totalPages,
            @NonNull @JsonProperty(required = true) Map<String, Wallpaper> wallpapers
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Request(@NonNull @JsonProperty(required = true) Query query) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Query(
                    @Nullable @JsonProperty("filter_date_day") Integer filterDateDay,
                    @Nullable @JsonProperty("filter_date_month") Integer filterDateMonth,
                    @Nullable @JsonProperty("filter_date_year") Integer filterDateYear,
                    @NonNull @JsonProperty(value = "filter_date_operator", required = true) Operator filterDateOperator,
                    @Nullable @JsonProperty("filter_gallery") List<Integer> filterGallery,
                    @Nullable @JsonProperty("filter_rating") Integer filterRating,
                    @Nullable @JsonProperty("filter_rating_operator") Operator filterRatingOperator,
                    @Nullable @JsonProperty("filter_res_operator_height") Operator filterResOperatorHeight,
                    @Nullable @JsonProperty("filter_res_operator_width") Operator filterResOperatorWidth,
                    @JsonProperty(value = "filter_res_height", required = true) int filterResHeight,
                    @NonNull @JsonProperty(value = "filter_res_operator", required = true) Operator filterResOperator,
                    @JsonProperty(value = "filter_res_width", required = true) int filterResWidth,
                    @Nullable @JsonProperty("filter_tag") List<Integer> filterTag,
                    @JsonProperty(required = true) int limit,
                    @NonNull @JsonProperty(required = true) Order order,
                    @NonNull @JsonProperty(value = "order_by", required = true) GetWallpapersOrderBy orderBy,
                    @JsonProperty(required = true) int page,
                    @Nullable String s,
                    @JsonProperty(value = "show_comments", required = true) boolean showComments,
                    @JsonProperty(value = "show_pickle_jar", required = true) boolean showPickleJar,
                    @JsonProperty(value = "show_resolutions", required = true) boolean showResolutions
            ) {
            }
        }
    }
}
