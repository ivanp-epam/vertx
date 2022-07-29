package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.response.error.ErrorObject;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerResponse;
import io.vertx.mutiny.ext.web.RoutingContext;

public class DefaultHandler implements ErrorHandlerSpecification {

    /**
     * Default Handler AlwaysTrue
     *
     * @param throwable
     * @return
     */
    @Override
    public boolean isSatisfy(RoutingContext event) {
        return event.failure() != null;
    }

    public void handle(RoutingContext event) {
        if (!isSatisfy(event)) {
            event.next();
            return;
        }

        HttpServerResponse response = event.response();
        response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.endAndForget(Json.encode(new ErrorResponse(new ErrorObject(123, event.failure().getMessage()))));
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
