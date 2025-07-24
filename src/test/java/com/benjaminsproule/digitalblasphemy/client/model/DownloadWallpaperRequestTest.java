package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DownloadWallpaperRequestTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void downloadWallpaperRequestBuilderRejectsWallpaperId(int wallpaperId) {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().wallpaperId(wallpaperId).width(1).height(1).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wallpaper ID must be greater than 0.");
    }

    @Test
    void downloadWallpaperRequestBuilderAcceptsWallpaperIdGreaterThan0() {
        assertThatCode(() -> DownloadWallpaperRequest.builder().wallpaperId(1).width(1).height(1).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void downloadWallpaperRequestBuilderRejectsWidth(int width) {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().width(width).wallpaperId(1).height(1).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width must be greater than 0.");
    }

    @Test
    void downloadWallpaperRequestBuilderAcceptsWidthGreaterThan0() {
        assertThatCode(() -> DownloadWallpaperRequest.builder().width(1).wallpaperId(1).height(1).build())
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void downloadWallpaperRequestBuilderRejectsHeight(int height) {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().height(height).wallpaperId(1).width(1).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height must be greater than 0.");
    }

    @Test
    void downloadWallpaperRequestBuilderAcceptsHeightGreaterThan0() {
        assertThatCode(() -> DownloadWallpaperRequest.builder().height(1).wallpaperId(1).width(1).build())
                .doesNotThrowAnyException();
    }

    @Test
    void downloadWallpaperRequestBuilderRejectsBuildWhenWallpaperIdNotProvided() {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().width(1).height(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Wallpaper ID must be provided.");
    }

    @Test
    void downloadWallpaperRequestBuilderRejectsBuildWhenWidthNotProvided() {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().wallpaperId(1).height(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Width must be provided.");
    }

    @Test
    void downloadWallpaperRequestBuilderRejectsBuildWhenHeightNotProvided() {
        assertThatThrownBy(() -> DownloadWallpaperRequest.builder().wallpaperId(1).width(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Height must be provided.");
    }

    @Test
    void downloadWallpaperRequestBuilderProvidesDefaults() {
        DownloadWallpaperRequest request = DownloadWallpaperRequest.builder().wallpaperId(1).width(1).height(1).build();

        assertThat(request.getType()).isEqualTo(WallpaperType.Single);
        assertThat(request.getWallpaperId()).isEqualTo(1);
        assertThat(request.getWidth()).isEqualTo(1);
        assertThat(request.getHeight()).isEqualTo(1);
        assertThat(request.isShowWatermark()).isEqualTo(true);
    }

    @Test
    void downloadWallpaperRequestBuilderOverridesDefaults() {
        DownloadWallpaperRequest request = DownloadWallpaperRequest.builder()
                .type(WallpaperType.Dual)
                .wallpaperId(2)
                .width(3)
                .height(4)
                .showWatermark(false)
                .build();

        assertThat(request.getType()).isEqualTo(WallpaperType.Dual);
        assertThat(request.getWallpaperId()).isEqualTo(2);
        assertThat(request.getWidth()).isEqualTo(3);
        assertThat(request.getHeight()).isEqualTo(4);
        assertThat(request.isShowWatermark()).isEqualTo(false);
    }

}
