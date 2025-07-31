package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OperatorTest {

    @MethodSource("ofArguments")
    @ParameterizedTest
    void ofReturnsCorrectType(String operator, Operator expectedOperator) {
        assertThat(Operator.of(operator)).isEqualTo(expectedOperator);
    }

    public static Stream<Arguments> ofArguments() {
        return Stream.of(
                arguments("=", Operator.EQUAL),
                arguments(">", Operator.GREATER_THAN),
                arguments(">=", Operator.GREATER_THAN_OR_EQUAL),
                arguments("<", Operator.LESS_THAN),
                arguments("<=", Operator.LESS_THAN_OR_EQUAL)
        );
    }

    @Test
    void ofThrowsExceptionIfInvalidType() {
        assertThatThrownBy(() -> Operator.of("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid is not a valid Operator");
    }

}
