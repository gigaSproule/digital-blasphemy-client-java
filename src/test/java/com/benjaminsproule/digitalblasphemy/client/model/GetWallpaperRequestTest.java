package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GetWallpaperRequestTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void getWallpaperRequestBuilderRejectsWallpaperId(int wallpaperId) {
        assertThatThrownBy(() -> GetWallpaperRequest.builder().wallpaperId(wallpaperId).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wallpaper ID must be greater than 0.");
    }

    @Test
    void getWallpaperRequestBuilderAcceptsWallpaperIdGreaterThan0() {
        assertThatCode(() -> GetWallpaperRequest.builder().wallpaperId(1).build())
                .doesNotThrowAnyException();
    }

    @Test
    void getWallpaperRequestBuilderRejectsBuildWhenWallpaperIdNotProvided() {
        assertThatThrownBy(() -> GetWallpaperRequest.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Wallpaper ID must be provided.");
    }

    @Test
    void getWallpaperRequestBuilderProvidesDefaults() {
        GetWallpaperRequest request = GetWallpaperRequest.builder().wallpaperId(1).build();

        assertThat(request.getWallpaperId()).isEqualTo(1);
        assertThat(request.getFilterResHeight()).isEqualTo(0);
        assertThat(request.getFilterResOperator()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResOperatorHeight()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResOperatorWidth()).isEqualTo(Operator.GreaterThanOrEqual);
        assertThat(request.getFilterResWidth()).isEqualTo(0);
        assertThat(request.isShowComments()).isEqualTo(false);
        assertThat(request.isShowPickleJar()).isEqualTo(false);
        assertThat(request.isShowResolutions()).isEqualTo(true);
    }

    @Test
    void getWallpaperRequestBuilderOverridesDefaults() {
        GetWallpaperRequest request = GetWallpaperRequest.builder()
                .wallpaperId(2)
                .filterResHeight(3)
                .filterResOperator(Operator.Equal)
                .filterResOperatorHeight(Operator.Equal)
                .filterResOperatorWidth(Operator.Equal)
                .filterResWidth(4)
                .showComments(true)
                .showPickleJar(true)
                .showResolutions(false)
                .build();

        assertThat(request.getWallpaperId()).isEqualTo(2);
        assertThat(request.getFilterResHeight()).isEqualTo(3);
        assertThat(request.getFilterResOperator()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResOperatorHeight()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResOperatorWidth()).isEqualTo(Operator.Equal);
        assertThat(request.getFilterResWidth()).isEqualTo(4);
        assertThat(request.isShowComments()).isEqualTo(true);
        assertThat(request.isShowPickleJar()).isEqualTo(true);
        assertThat(request.isShowResolutions()).isEqualTo(false);
    }

}
