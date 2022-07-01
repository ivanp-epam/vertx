package com.programm.vertx.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class JsonHelperTest {
    private static Stream<Arguments> JsonMalformedDataProvider() {
        return Stream.of(
                Arguments.of("{", false),
                Arguments.of("{ \" }", false)
        );
    }

    private static Stream<Arguments> JsonOKDataProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("  ", true),
                Arguments.of("{ \"a\":\"b\" }", true),
                Arguments.of("[]", true)
        );
    }
}
