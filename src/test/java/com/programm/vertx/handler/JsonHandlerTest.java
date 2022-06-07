package com.programm.vertx.handler;

import com.programm.vertx.http.StatusCodes;
import com.programm.vertx.response.ErrorResponse;
import com.programm.vertx.response.ErrorResponseWrapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static io.vertx.core.json.Json.encodeToBuffer;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonHandlerTest {
    @Mock
    RoutingContext event;
    @Mock
    RequestBody requestBody;

    @Mock
    HttpServerResponse response;

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


    @ParameterizedTest
    @MethodSource("JsonMalformedDataProvider")
    void malformedJsonPath(String input, boolean malformed) {
        // Given
        when(requestBody.asString()).thenReturn(input);
        when(event.body()).thenReturn(requestBody);
        when(event.response()).thenReturn(response);

        // When
        new JsonHandler().handle(event);

        // Then
        assertFalse(malformed);
        verify(event, never()).next();
        verify(response, atLeastOnce()).setStatusCode(StatusCodes.BAD_REQUEST);
        verify(response, atLeastOnce()).putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        verify(response, atLeastOnce()).end(encodeToBuffer(new ErrorResponseWrapper(ErrorResponse.MALFORMED_JSON())));
    }

    @ParameterizedTest
    @MethodSource("JsonOKDataProvider")
    void OKJsonPath(String input, boolean malformed) {
        // Given
        when(requestBody.asString()).thenReturn(input);
        when(event.body()).thenReturn(requestBody);

        // When
        new JsonHandler().handle(event);

        // Then
        assertTrue(malformed);
        verify(event, atLeastOnce()).next();
        verify(response, never()).setStatusCode(StatusCodes.BAD_REQUEST);
    }
}