package com.programm.vertx.repository.inmemory;

import com.programm.vertx.dto.UserFilter;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponseWrapper;
import com.programm.vertx.response.UserResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {

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
        if (!users.containsKey(id)) {
            return null;
        }

        User user = users.get(id);

        if (user.isDeleted()) {
            return null;
        }

        return user;
    }

    @Override
    public User get(String id) throws EntityNotFoundException {
        User user = this.find(id);
        if (user == null) {
            throw new EntityNotFoundException();
        }
        return user;
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

    @Override
    public ResponseWrapper<Map<String, UserResponse>> findByPrefix(UserFilter filter) {
        Map<String, User> all = this.findAll();

        String startsFrom = filter.getStartFrom();
        int offset = filter.getOffset();
        int limit = filter.getLimit();

        if (startsFrom != null) {
            String startsFromLowerCase = startsFrom.toLowerCase();
            all = all.entrySet().stream()
                    .filter(entry -> entry.getValue().getLogin().toLowerCase().startsWith(startsFromLowerCase))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        long total = all.size();

        Map<String, UserResponse> collect = all.entrySet().stream()
                .skip(offset).limit(limit)
                .map(entry -> Map.entry(entry.getKey(), UserResponse.from(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ResponseWrapper<>(collect,
                new Pagination(total, offset, limit));
    }
}
