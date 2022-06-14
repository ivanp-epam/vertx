package com.programm.vertx.handler;

import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.request.UserRequest;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.HttpException;
import com.programm.vertx.repository.inmemory.UserRepository;
import com.programm.vertx.response.UserResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class UsersHandler {
    private final UserRepository repository;

    public UsersHandler(UserRepository repository) {
        this.repository = repository;
    }

    public void getAll(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();

        UsersFilterRequest usersFilterRequest = new UsersFilterRequest(
                request.getParam("startsFrom"),
                request.getParam("limit"),
                request.getParam("offset")
        );

        ctx.json(repository.findByPrefix(usersFilterRequest));
    }

    public void create(RoutingContext ctx) throws HttpException {
        UserRequest userRequest = ctx.body().asPojo(UserRequest.class);
        User dto = repository.add(User.from(userRequest));

        ctx.json(UserResponse.from(dto));
    }

    public void get(RoutingContext ctx) throws HttpException {
        User dto = repository.get(ctx.pathParam("id"));
        ctx.json(UserResponse.from(dto));
    }

    public void put(RoutingContext ctx) throws HttpException {
        String uuid = ctx.pathParam("id");

        User user = repository.get(uuid);
        UserRequest userRequest = ctx.body().asPojo(UserRequest.class);
        User updatedUser = user.with(userRequest);

        repository.update(updatedUser);

        ctx.json(UserResponse.from(updatedUser));
    }

    public void delete(RoutingContext ctx) throws HttpException {
        String id = ctx.pathParam("id");

        User dto = repository.get(id);
        repository.delete(dto);

        ctx.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
    }
}
