package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetWallpaperRequestTest {

    @Test
    void getWallpaperRequestBuilderRejectsBuildWhenWallpaperIdNotProvided() {
        assertThatThrownBy(() -> GetWallpaperRequest.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Wallpaper ID must be provided.");
    }

}