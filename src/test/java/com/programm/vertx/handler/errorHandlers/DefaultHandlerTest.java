package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.response.error.ErrorObject;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
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
public class DefaultHandlerTest {

    private final DefaultHandler handler = new DefaultHandler();

    @Mock
    RoutingContext event;

    @Mock
    HttpServerResponse response;

    @Test
    void testHandlerPassThroughIfNoneExceptionsIsProvided() {
        // given + when
        handler.accept(event);

        // then
        verify(event, atLeastOnce()).next();
    }


    @Test
    void testHandlerHandleOnAnyThrowable() {
        // given
        Throwable ex = new Throwable();
        when(event.failure()).thenReturn(ex);
        when(event.response()).thenReturn(response);

        // when
        handler.accept(event);

        // then
        verify(response, atLeastOnce()).setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        verify(response, atLeastOnce()).putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        verify(response, atLeastOnce()).endAndForget(Json.encode(new ErrorResponse(new ErrorObject(123, event.failure().getMessage()))));
    }
}
