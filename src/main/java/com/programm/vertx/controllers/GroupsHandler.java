package com.programm.vertx.controllers;

import com.programm.vertx.entities.Group;
import com.programm.vertx.exceptions.HttpException;
import com.programm.vertx.repository.IGroupRepository;
import com.programm.vertx.request.GroupRequest;
import com.programm.vertx.response.GroupResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerRequest;
import io.vertx.mutiny.ext.web.RoutingContext;

public class GroupsHandler {
    private final IGroupRepository repository;

    public GroupsHandler(IGroupRepository repository) {
        this.repository = repository;
    }

    public void getAll(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();
        int limit = Integer.parseInt(request.getParam("limit", "10"));
        int offset = Integer.parseInt(request.getParam("offset", "0"));

        repository.findPaginated(limit, offset)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void create(RoutingContext ctx) throws HttpException {
        GroupRequest groupRequest = Json.decodeValue(ctx.body().getDelegate().buffer(), GroupRequest.class);
        Uni<Group> dto = repository.add(Group.from(groupRequest));

        dto.map(GroupResponse::from)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void get(RoutingContext ctx) throws HttpException {
        Uni<Group> dto = repository.get(ctx.pathParam("id"));

        dto.map(GroupResponse::from)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void put(RoutingContext ctx) throws HttpException {
        String uuid = ctx.pathParam("id");

        Uni<Group> user = repository.get(uuid);

        GroupRequest groupRequest = Json.decodeValue(ctx.body().getDelegate().buffer(), GroupRequest.class);

        Uni<Group> invoke = user
                .map(el -> Group.from(groupRequest).setId(el.getId()))
                .call(repository::update);

        invoke
                .map(GroupResponse::from).subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void delete(RoutingContext ctx) throws HttpException {
        String id = ctx.pathParam("id");
        Uni<Group> dto = repository.get(id);
        dto.onItem().call(repository::delete)
                .subscribe().with((el) -> {
                    ctx.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).endAndForget();
                }, ctx::fail);
    }
}
