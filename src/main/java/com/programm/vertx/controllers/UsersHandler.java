package com.programm.vertx.controllers;

import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.HttpException;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UserRequest;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerRequest;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.Map;

public class UsersHandler {
    private final IUserRepository repository;

    public UsersHandler(IUserRepository repository) {
        this.repository = repository;
    }

    public void getAll(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();

        UsersFilterRequest usersFilterRequest = new UsersFilterRequest(
                request.getParam("startsFrom"),
                request.getParam("limit"),
                request.getParam("offset")
        );

        Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> byPrefix = repository.findByPrefix(usersFilterRequest);

        byPrefix
                .onFailure().invoke(ctx::fail)
                .subscribe().with(ctx::jsonAndForget);
    }

    public void create(RoutingContext ctx) throws HttpException {
        UserRequest userRequest = Json.decodeValue(ctx.getBody().getDelegate(), UserRequest.class);
        Uni<User> dto = repository.add(User.from(userRequest));

        dto.map(UserResponse::from)
                .onFailure().invoke(ctx::fail)
                .subscribe().with(ctx::jsonAndForget);
    }

    public void get(RoutingContext ctx) throws HttpException {
        Uni<User> dto = repository.get(ctx.pathParam("id"));

        dto.map(UserResponse::from)
                .onFailure().invoke(ctx::fail)
                .subscribe().with(ctx::jsonAndForget);
    }

    public void put(RoutingContext ctx) throws HttpException {
        String uuid = ctx.pathParam("id");

        Uni<User> user = repository.get(uuid);
        UserRequest userRequest = Json.decodeValue(ctx.getBody().getDelegate(), UserRequest.class);

        Uni<User> invoke = user
                .map(userEl -> userEl.with(userRequest))
                .call(repository::update);

        invoke
                .onFailure().invoke(ctx::fail)
                .map(UserResponse::from).subscribe().with(ctx::jsonAndForget);
    }

    public void delete(RoutingContext ctx) throws HttpException {
        String id = ctx.pathParam("id");

        Uni<User> dto = repository.get(id);
        dto.onItem().call(repository::delete)
                .onFailure().invoke(ctx::fail)
                .subscribe().with((el) -> {
                    ctx.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).endAndForget();
                });
    }
}
