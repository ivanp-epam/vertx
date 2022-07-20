package com.programm.vertx.repository;

public interface IRepositoryManager {

    IGroupRepository getGroupRepository();

    IUserRepository getUserRepository();

    IUserGroupRepository getUserGroupRepository();

    IAuthRepository getAuthRepository();
}
