package com.programm.vertx.handler.errorHandlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface RouteHandlerSpecification extends Handler<RoutingContext> {

    public boolean isSatisfy(RoutingContext event);
}
