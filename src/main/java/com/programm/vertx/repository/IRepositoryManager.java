package com.programm.vertx.repository;

import com.programm.vertx.entities.Group;

public interface IRepositoryManager {

    public IGroupRepository getGroupRepository();

    public IUserRepository getUserRepository();

    public IUserGroupRepository getUserGroupRepository();
}
