package com.programm.vertx.repository;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

public interface IUserRepository extends IRepository<User> {

    Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> findByPrefix(UsersFilterRequest filter);

    Uni<List<User>> findByIds(List<String> ids);

    Uni<List<User>> getUsersByGroup(Group user);
}
