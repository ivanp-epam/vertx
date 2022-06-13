package com.programm.vertx.handler.errorHandlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class RouteHandlerManager implements Handler<RoutingContext> {

    private final List<RouteHandlerSpecification> handlers = List.of(new HttpErrorHandler());

    private final RouteHandlerSpecification defaultHandler = new DefaultHandler();

    @Override
    public void handle(RoutingContext event) {
        for (RouteHandlerSpecification handler : handlers) {

            if (!handler.isSatisfy(event)) {
                continue;
            }

            handler.handle(event);
            return;
        }

        // fire default error handler if none handler found previously
        defaultHandler.handle(event);
    }
}
