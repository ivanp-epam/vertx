package com.programm.vertx.handler.errorHandlers;

import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.List;
import java.util.function.Consumer;

public class RouteHandlerManager implements Consumer<RoutingContext> {

    private final List<RouteHandlerSpecification> handlers = List.of(new HttpErrorHandler());

    private final RouteHandlerSpecification defaultHandler = new DefaultHandler();

    public void handle(RoutingContext event) {
        for (RouteHandlerSpecification handler : handlers) {

            if (!handler.isSatisfy(event)) {
                continue;
            }

            handler.accept(event);
            return;
        }

        // fire default error handler if none handler found previously
        defaultHandler.accept(event);
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
