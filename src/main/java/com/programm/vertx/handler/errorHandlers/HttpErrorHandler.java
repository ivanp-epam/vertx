package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.exceptions.HttpException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerResponse;
import io.vertx.mutiny.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpErrorHandler implements ErrorHandlerSpecification {

    @Override
    public boolean isSatisfy(RoutingContext event) {
        return event.failure() instanceof HttpException;
    }

    public void handle(RoutingContext event) {
        if (!isSatisfy(event)) {
            event.next();
            return;
        }

        HttpServerResponse response = event.response();

        HttpException ex = (HttpException) event.failure();
        response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.setStatusCode(ex.getHttpCode());
        response.endAndForget(Json.encode(ex.response()));
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
