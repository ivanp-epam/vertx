package com.programm.vertx.handler;

import com.programm.vertx.exceptions.MalformedJsonException;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import static com.programm.vertx.helper.JsonHelper.isJSONValid;

public class JsonHandler implements Handler<RoutingContext> {

    public void handle(RoutingContext ctx) {
        if (isJSONValid(ctx.body().asString())) {
            ctx.next();
            return;
        }

        throw new MalformedJsonException();
    }
}
