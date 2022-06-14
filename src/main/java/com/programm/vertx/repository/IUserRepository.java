package com.programm.vertx.repository;

import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.entities.User;
import com.programm.vertx.response.ResponseWrapper;
import com.programm.vertx.response.UserResponse;

import java.util.Map;

public interface IUserRepository extends IRepository<User> {

    ResponseWrapper<Map<String, UserResponse>> findByPrefix(UsersFilterRequest filter);
}
