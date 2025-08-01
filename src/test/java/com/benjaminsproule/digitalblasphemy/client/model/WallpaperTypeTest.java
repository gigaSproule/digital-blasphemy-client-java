package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WallpaperTypeTest {

    @MethodSource("toStringArguments")
    @ParameterizedTest
    void toStringReturnsCorrectValue(WallpaperType wallpaperType, String expectedValue) {
        assertThat(wallpaperType.toString()).isEqualTo(expectedValue);
    }

    public static Stream<Arguments> toStringArguments() {
        return Stream.of(
                arguments(WallpaperType.SINGLE, "single"),
                arguments(WallpaperType.DUAL, "dual"),
                arguments(WallpaperType.TRIPLE, "triple"),
                arguments(WallpaperType.MOBILE, "mobile")
        );
    }

    @MethodSource("ofArguments")
    @ParameterizedTest
    void ofReturnsCorrectType(String type, WallpaperType expectedWallpaperType) {
        assertThat(WallpaperType.of(type)).isEqualTo(expectedWallpaperType);
    }

    public static Stream<Arguments> ofArguments() {
        return Stream.of(
                arguments("single", WallpaperType.SINGLE),
                arguments("dual", WallpaperType.DUAL),
                arguments("triple", WallpaperType.TRIPLE),
                arguments("mobile", WallpaperType.MOBILE)
        );
    }

    @Test
    void ofThrowsExceptionIfInvalidType() {
        assertThatThrownBy(() -> WallpaperType.of("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid is not a valid WallpaperType");
    }

}
