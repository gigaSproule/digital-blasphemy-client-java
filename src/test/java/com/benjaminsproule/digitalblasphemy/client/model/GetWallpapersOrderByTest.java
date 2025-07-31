package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GetWallpapersOrderByTest {

    @MethodSource("ofArguments")
    @ParameterizedTest
    void ofReturnsCorrectType(String orderBy, GetWallpapersOrderBy expectedOperator) {
        assertThat(GetWallpapersOrderBy.of(orderBy)).isEqualTo(expectedOperator);
    }

    public static Stream<Arguments> ofArguments() {
        return Stream.of(
                arguments("date", GetWallpapersOrderBy.DATE),
                arguments("name", GetWallpapersOrderBy.NAME)
        );
    }

    @Test
    void ofThrowsExceptionIfInvalidType() {
        assertThatThrownBy(() -> GetWallpapersOrderBy.of("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid is not a valid GetWallpapersOrderBy");
    }

}
