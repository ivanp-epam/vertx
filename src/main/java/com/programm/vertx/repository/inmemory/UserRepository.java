package com.programm.vertx.repository.inmemory;

import com.programm.vertx.dto.UserDto;
import com.programm.vertx.repository.IRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepository implements IRepository<UserDto> {

    private Map<String, UserDto> users = new HashMap<>();

    @Override
    public Map<String, UserDto> findAll() {
        return users.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isDeleted())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public UserDto find(String id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public UserDto add(UserDto entity) {
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean delete(UserDto entity) {
        entity.setDeleted(true);
        users.put(entity.getId(), entity);

        return true;
    }

    @Override
    public UserDto update(UserDto entity) {
        return users.replace(entity.getId(), entity);
    }

    public void clear() {
        users = new HashMap<>();
    }
}
