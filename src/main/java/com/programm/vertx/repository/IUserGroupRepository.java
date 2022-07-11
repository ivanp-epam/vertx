package com.programm.vertx.repository;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IUserGroupRepository {

    Uni<Void> addUsersToGroup(String groupId, List<String> userIds);

    Uni<Void> removeUserFromGroup(String groupId, String userId);

    Uni<Void> removeAllUsersFromGroup(String groupId);

    Uni<Void> setUsersToGroup(String groupId, List<String> userIds);

    Uni<Void> checkUserGroup(String groupId, String userId);
}
