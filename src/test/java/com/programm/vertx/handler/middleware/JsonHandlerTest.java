package com.programm.vertx.handler.middleware;

import com.programm.vertx.exceptions.MalformedJsonException;
import io.vertx.mutiny.ext.web.RequestBody;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonHandlerTest {
    @Mock
    RoutingContext event;

    @Mock
    RequestBody requestBody;

    @Test
    void testJsonHandlerThrowsErrorOnMalformedJSON() {
        // given
        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn("]");

        // when
        JsonHandler jsonHandler = new JsonHandler();

        // then
        Assertions.assertThrows(MalformedJsonException.class, () -> {
            jsonHandler.accept(event);
        });
    }

    @Test
    void testJsonHandlerPassThroughCorrectJSON() {
        // given
        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn("[]");

        // when
        JsonHandler jsonHandler = new JsonHandler();

        // then
        jsonHandler.accept(event);
        verify(event, atLeastOnce()).next();
    }

    @Test
    void testJsonHandlerPassThroughNull() {
        // given
        when(event.body()).thenReturn(requestBody);
        when(requestBody.asString()).thenReturn(null);
        JsonHandler jsonHandler = new JsonHandler();

        // when
        jsonHandler.accept(event);

        // then
        verify(event, atLeastOnce()).next();
    }
}