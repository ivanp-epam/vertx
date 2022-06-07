package com.programm.vertx.http;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;

public class ResponseHelper {

    public static void json(HttpServerResponse response, int StatusCode, Object obj) {
        response.setStatusCode(StatusCode);
        Buffer buffer = Json.encodeToBuffer(obj);
        response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        response.end(buffer);
    }

    public static void json(HttpServerResponse response, Object obj) {
        json(response, StatusCodes.OK, obj);
    }
}
