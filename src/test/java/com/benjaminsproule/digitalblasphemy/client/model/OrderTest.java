package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OrderTest {

    @MethodSource("ofArguments")
    @ParameterizedTest
    void ofReturnsCorrectType(String order, Order expectedOrder) {
        assertThat(Order.of(order)).isEqualTo(expectedOrder);
    }

    public static Stream<Arguments> ofArguments() {
        return Stream.of(
                arguments("asc", Order.ASCENDING),
                arguments("desc", Order.DESCENDING)
        );
    }

    @Test
    void ofThrowsExceptionIfInvalidType() {
        assertThatThrownBy(() -> Order.of("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid is not a valid Order");
    }

}
