package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.exceptions.HttpException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class HttpErrorHandler implements RouteHandlerSpecification {

    @Override
    public boolean isSatisfy(RoutingContext event) {
        return event.failure() instanceof HttpException;
    }

    @Override
    public void handle(RoutingContext event) {
        HttpServerResponse response = event.response();

        HttpException ex = (HttpException) event.failure();
        response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.setStatusCode(ex.getHttpCode());
        response.end(Json.encodeToBuffer(ex.response()));
    }
}
