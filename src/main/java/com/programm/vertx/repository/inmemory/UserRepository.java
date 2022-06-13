package com.programm.vertx.repository.inmemory;

import com.programm.vertx.entities.User;
import com.programm.vertx.repository.IRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepository implements IRepository<User> {

    private Map<String, User> users = new HashMap<>();

    @Override
    public Map<String, User> findAll() {
        return users.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isDeleted())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public User find(String id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public User add(User entity) {
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean delete(User entity) {
        entity.setDeleted(true);
        users.put(entity.getId(), entity);

        return true;
    }

    @Override
    public User update(User entity) {
        return users.replace(entity.getId(), entity);
    }

    public void clear() {
        users = new HashMap<>();
    }
}
