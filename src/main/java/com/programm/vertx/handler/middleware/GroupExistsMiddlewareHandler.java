package com.programm.vertx.handler.middleware;

import com.programm.vertx.repository.IGroupRepository;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

public class GroupExistsMiddlewareHandler implements Consumer<RoutingContext> {

    private final IGroupRepository repository;

    public GroupExistsMiddlewareHandler(IGroupRepository repository) {
        this.repository = repository;
    }

    public void handle(RoutingContext ctx) {
        String uuid = ctx.pathParam("groupId");
        if (uuid == null) {
            ctx.next();
            return;
        }

        repository.get(uuid)
                .subscribe().with((notUsed) -> ctx.next(), ctx::fail);
    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
