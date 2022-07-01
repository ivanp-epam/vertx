package com.programm.vertx.handler;

import com.programm.vertx.exceptions.MalformedJsonException;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

import static com.programm.vertx.helper.JsonHelper.isJSONValid;

public class JsonHandler implements Consumer<RoutingContext> {

    public void handle(RoutingContext ctx) {
        if (isJSONValid(ctx.getBodyAsString())) {
            ctx.next();
            return;
        }

        throw new MalformedJsonException();
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
