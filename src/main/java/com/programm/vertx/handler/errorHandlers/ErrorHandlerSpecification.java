package com.programm.vertx.handler.errorHandlers;

import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

public interface ErrorHandlerSpecification extends Consumer<RoutingContext> {

    boolean isSatisfy(RoutingContext event);
}
