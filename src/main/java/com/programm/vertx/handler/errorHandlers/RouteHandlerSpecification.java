package com.programm.vertx.handler.errorHandlers;

import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

public interface RouteHandlerSpecification extends Consumer<RoutingContext> {

    public boolean isSatisfy(RoutingContext event);
}
