package com.benjaminsproule.digitalblasphemy.client.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
class GetWallpapersRequestTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 32})
    void getWallpaperRequestBuilderRejectsFilterDateDay(int filterDateDay) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().filterDateDay(filterDateDay).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Filter date day must be between 1 and 31.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 31})
    void getWallpapersRequestBuilderAcceptsFilterDateDay(int filterDateDay) {
        assertThatCode(() -> GetWallpapersRequest.builder().filterDateDay(filterDateDay).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 13})
    void getWallpaperRequestBuilderRejectsFilterDateMonth(int filterDateMonth) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().filterDateMonth(filterDateMonth).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Filter date month must be between 1 and 12.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 12})
    void getWallpapersRequestBuilderAcceptsFilterDateMonth(int filterDateMonth) {
        assertThatCode(() -> GetWallpapersRequest.builder().filterDateMonth(filterDateMonth).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1996})
    void getWallpaperRequestBuilderRejectsFilterDateYear(int filterDateYear) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().filterDateYear(filterDateYear).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Filter date year must be from 1997 inclusive.");
    }

    @Test
    void getWallpapersRequestBuilderAcceptsFilterDateYearGreaterThan1996() {
        assertThatCode(() -> GetWallpapersRequest.builder().filterDateYear(1997).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(floats = {-1, 0, 5.01f})
    void getWallpaperRequestBuilderRejectsFilterRating(float filterRating) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().filterRating(filterRating).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Filter rating must be between 1 and 5.");
    }

    @ParameterizedTest
    @ValueSource(floats = {1, 5})
    void getWallpapersRequestBuilderAcceptsFilterRating(float filterRating) {
        assertThatCode(() -> GetWallpapersRequest.builder().filterRating(filterRating).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 51})
    void getWallpaperRequestBuilderRejectsLimit(int limit) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().limit(limit).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Limit must be between 1 and 50.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50})
    void getWallpapersRequestBuilderAcceptsLimit(int limit) {
        assertThatCode(() -> GetWallpapersRequest.builder().limit(limit).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void getWallpaperRequestBuilderRejectsPage(int page) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().page(page).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page must be greater than 0.");
    }

    @Test
    void getWallpapersRequestBuilderAcceptsPage() {
        assertThatCode(() -> GetWallpapersRequest.builder().page(1).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void getWallpaperRequestBuilderRejectsS(String s) {
        assertThatThrownBy(() -> GetWallpapersRequest.builder().s(s).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("S must not be an empty or blank string.");
    }

    @Test
    void getWallpapersRequestBuilderAcceptsS() {
        assertThatCode(() -> GetWallpapersRequest.builder().s("search").build())
                .doesNotThrowAnyException();
    }

    @Test
    void getWallpapersRequestBuilderProvidesDefaults() {
        GetWallpapersRequest request = GetWallpapersRequest.builder().build();

        assertThat(request.getFilterDateDay()).isEqualTo(0);
        assertThat(request.getFilterDateMonth()).isEqualTo(0);
        assertThat(request.getFilterDateYear()).isEqualTo(0);
        assertThat(request.getFilterDateOperator()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterGallery()).isEqualTo(emptyList());
        assertThat(request.getFilterRating()).isEqualTo(0f);
        assertThat(request.getFilterRatingOperator()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResHeight()).isEqualTo(0);
        assertThat(request.getFilterResOperator()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResOperatorHeight()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResOperatorWidth()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResWidth()).isEqualTo(0);
        assertThat(request.getFilterTag()).isEqualTo(emptyList());
        assertThat(request.getLimit()).isEqualTo(10);
        assertThat(request.getOrder()).isEqualTo(Order.ASCENDING);
        assertThat(request.getOrderBy()).isEqualTo(GetWallpapersOrderBy.DATE);
        assertThat(request.getPage()).isEqualTo(1);
        assertThat(request.getS()).isEqualTo("");
        assertThat(request.isShowComments()).isEqualTo(false);
        assertThat(request.isShowPickleJar()).isEqualTo(false);
        assertThat(request.isShowResolutions()).isEqualTo(true);
    }

    @Test
    void getWallpapersRequestBuilderOverridesDefaults() {
        GetWallpapersRequest request = GetWallpapersRequest.builder()
                .filterDateDay(2)
                .filterDateMonth(2)
                .filterDateYear(2000)
                .filterDateOperator(Operator.Equal)
                .filterGallery(List.of(1, 2))
                .filterRating(2.5f)
                .filterRatingOperator(Operator.Equal)
                .filterResHeight(1080)
                .filterResOperator(Operator.Equal)
                .filterResOperatorHeight(Operator.Equal)
                .filterResOperatorWidth(Operator.Equal)
                .filterResWidth(1920)
                .filterTag(List.of(1, 2))
                .limit(20)
                .order(Order.DESCENDING)
                .orderBy(GetWallpapersOrderBy.NAME)
                .page(5)
                .s("search")
                .showComments(true)
                .showPickleJar(true)
                .showResolutions(false)
                .build();

        assertThat(request.getFilterDateDay()).isEqualTo(2);
        assertThat(request.getFilterDateMonth()).isEqualTo(2);
        assertThat(request.getFilterDateYear()).isEqualTo(2000);
        assertThat(request.getFilterDateOperator()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterGallery()).isEqualTo(List.of(1, 2));
        assertThat(request.getFilterRating()).isEqualTo(2.5f);
        assertThat(request.getFilterRatingOperator()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResHeight()).isEqualTo(1080);
        assertThat(request.getFilterResOperator()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResOperatorHeight()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResOperatorWidth()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResWidth()).isEqualTo(1920);
        assertThat(request.getFilterTag()).isEqualTo(List.of(1, 2));
        assertThat(request.getLimit()).isEqualTo(20);
        assertThat(request.getOrder()).isEqualTo(Order.DESCENDING);
        assertThat(request.getOrderBy()).isEqualTo(GetWallpapersOrderBy.NAME);
        assertThat(request.getPage()).isEqualTo(5);
        assertThat(request.getS()).isEqualTo("search");
        assertThat(request.isShowComments()).isEqualTo(true);
        assertThat(request.isShowPickleJar()).isEqualTo(true);
        assertThat(request.isShowResolutions()).isEqualTo(false);
    }

}
