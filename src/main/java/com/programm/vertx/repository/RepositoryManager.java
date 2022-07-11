package com.programm.vertx.repository;

import com.programm.vertx.repository.pgclient.GroupRepository;
import com.programm.vertx.repository.pgclient.UserGroupRepository;
import com.programm.vertx.repository.pgclient.UserRepository;
import io.vertx.mutiny.pgclient.PgPool;

public class RepositoryManager implements IRepositoryManager {

    private final PgPool client;

    public RepositoryManager(PgPool client) {

        this.client = client;
    }

    public IUserRepository getUserRepository() {
        return new UserRepository(client, this);
    }

    @Override
    public IUserGroupRepository getUserGroupRepository() {
        return new UserGroupRepository(client);
    }

    public IGroupRepository getGroupRepository() {
        return new GroupRepository(client, this);
    }
}
