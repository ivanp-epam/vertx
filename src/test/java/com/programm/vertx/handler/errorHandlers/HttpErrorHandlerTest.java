package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.exceptions.EntityNotFoundException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerResponse;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpErrorHandlerTest {

    private final HttpErrorHandler handler = new HttpErrorHandler();

    @Mock
    RoutingContext event;

    @Mock
    HttpServerResponse response;

    @Test
    void testHandlerPassThroughIfExceptionIsNotMatch() {
        // given
        when(event.failure()).thenReturn(new RuntimeException());

        // when
        handler.accept(event);

        // then
        verify(event, atLeastOnce()).next();
    }

    @Test
    void testHandlerHandleErrorIfExceptionMatch() {
        // given
        EntityNotFoundException ex = new EntityNotFoundException();
        when(event.failure()).thenReturn(ex);
        when(event.response()).thenReturn(response);

        // when
        handler.accept(event);

        // then

        verify(response, atLeastOnce()).putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        verify(response, atLeastOnce()).setStatusCode(ex.getHttpCode());
        verify(response, atLeastOnce()).endAndForget(Json.encode(ex.response()));
    }
}
