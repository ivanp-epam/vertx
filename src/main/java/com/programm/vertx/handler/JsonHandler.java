package com.programm.vertx.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programm.vertx.http.ResponseHelper;
import com.programm.vertx.http.StatusCodes;
import com.programm.vertx.response.ErrorResponse;
import com.programm.vertx.response.ErrorResponseWrapper;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;

public class JsonHandler implements Handler<RoutingContext> {
    public static boolean isJSONValid(String jsonInString) {
        // mark null value as valid JSON
        if (jsonInString == null) {
            return true;
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void handle(RoutingContext ctx) {
        if (isJSONValid(ctx.body().asString())) {
            ctx.next();
            return;
        }

        ResponseHelper.json(
                ctx.response(),
                StatusCodes.BAD_REQUEST,
                new ErrorResponseWrapper(ErrorResponse.MALFORMED_JSON())
        );
    }
}
