package com.programm.vertx.handler.errorHandlers;

import com.programm.vertx.response.error.ErrorCodes;
import com.programm.vertx.response.error.ErrorObject;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class DefaultHandler implements RouteHandlerSpecification{

    /**
     * Default Handler AlwaysTrue
     * @param throwable
     * @return
     */
    @Override
    public boolean isSatisfy(RoutingContext event) {
        return true;
    }

    @Override
    public void handle(RoutingContext event) {
        HttpServerResponse response = event.response();
        response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
//        response.end(Json.encodeToBuffer(ErrorResponse.create(ErrorCodes.INTERNAL_SERVER_ERROR)));
        event.failure().printStackTrace();
        response.end(Json.encodeToBuffer(new ErrorResponse(new ErrorObject(123, event.failure().getMessage()))));
    }
}
