package com.programm.vertx.repository;

import com.programm.vertx.repository.pgclient.GroupRepository;

public interface IRepositoryManager {

    public GroupRepository getGroupRepository();
}
