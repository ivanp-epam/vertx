package com.programm.vertx.repository;

import com.programm.vertx.repository.pgclient.GroupRepository;

public class RepositoryManager implements IRepositoryManager {

    private final GroupRepository groupRepository;

    public RepositoryManager(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public GroupRepository getGroupRepository() {
        return groupRepository;
    }
}
