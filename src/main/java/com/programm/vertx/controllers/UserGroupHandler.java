package com.programm.vertx.controllers;

import com.programm.vertx.entities.Group;
import com.programm.vertx.exceptions.BadRequestException;
import com.programm.vertx.repository.IUserGroupRepository;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UserIdsRequest;
import com.programm.vertx.response.GroupResponse;
import com.programm.vertx.response.ResponseWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServerRequest;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.stream.Collectors;

public class UserGroupHandler {

    private final IUserGroupRepository userGroupRepository;
    private final IUserRepository userRepository;

    public UserGroupHandler(IUserGroupRepository userGroupRepository, IUserRepository userRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
    }

    public void getUsers(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();
        String groupId = request.getParam("groupId");
        Group group = new Group(groupId);

        userRepository.getUsersByGroup(group)
                .map(users -> users.stream().map(GroupResponse.UserResponse::new).collect(Collectors.toList()))
                .map(ResponseWrapper::new)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void getUser(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();
        String groupId = ctx.pathParam("groupId");
        String userId = request.getParam("userId");

        userGroupRepository
                .checkUserGroup(groupId, userId)
                .onItem().transformToUni((notUsed) -> userRepository.get(userId))
                .map(GroupResponse.UserResponse::new)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void updateUsers(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();
        String groupId = request.getParam("groupId");
        UserIdsRequest userIdsRequest = Json.decodeValue(ctx.body().getDelegate().buffer(), UserIdsRequest.class);


        userRepository.findByIds(userIdsRequest.getUserIds())
                .map(Unchecked.function(users -> {
                    if (users.size() != userIdsRequest.getUserIds().size()) {
                        throw new BadRequestException();
                    }
                    return users;
                })).onItem().transformToUni((notUsed) -> userGroupRepository.setUsersToGroup(groupId, userIdsRequest.getUserIds()))
                .onItem().transformToUni((notUsed) -> userRepository.getUsersByGroup(new Group(groupId)))
                .map(users -> users.stream().map(GroupResponse.UserResponse::new).collect(Collectors.toList()))
                .map(ResponseWrapper::new)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }

    public void deleteUsersFromGroup(RoutingContext ctx) {
        String groupId = ctx.pathParam("groupId");

        userGroupRepository
                .removeAllUsersFromGroup(groupId)
                .subscribe().with((el) -> {
                    ctx.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).endAndForget();
                }, ctx::fail);
    }

    public void deleteParticularUsersFromGroup(RoutingContext ctx) {
        String groupId = ctx.pathParam("groupId");
        String userId = ctx.pathParam("userId");


        userGroupRepository
                .checkUserGroup(groupId, userId)
                .onItem().transformToUni((notUsed) -> userGroupRepository.removeUserFromGroup(groupId, userId))
                .subscribe().with((el) -> {
                    ctx.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).endAndForget();
                }, ctx::fail);
    }
}
