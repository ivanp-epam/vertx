package com.programm.vertx.repository.inmemory;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Uni;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {

    private Map<String, User> users = new HashMap<>();

    @Override
    public Uni<Map<String, User>> findAll() {
        Map<String, User> collect = users.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isDeleted())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Uni.createFrom().item(collect);
    }

    @Override
    public Uni<User> find(String id) {
        if (!users.containsKey(id)) {
            return Uni.createFrom().nullItem();
        }

        User user = users.get(id);

        if (user.isDeleted()) {
            return Uni.createFrom().nullItem();
        }

        return Uni.createFrom().item(user);
    }

    @Override
    public Uni<List<User>> findByIds(List<String> ids) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Uni<User> get(String id) throws EntityNotFoundException {
        return this.find(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @Override
    public Uni<User> add(User entity) {
        users.put(entity.getStringId(), entity);
        return Uni.createFrom().item(entity);
    }

    @Override
    public Uni<Void> delete(User entity) {
        entity.setDeleted(true);
        users.put(entity.getStringId(), entity);

        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<User> update(User entity) {
        User replace = users.replace(entity.getStringId(), entity);
        return Uni.createFrom().item(replace);
    }

    public void clear() {
        users = new HashMap<>();
    }

    @Override
    public Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> findByPrefix(UsersFilterRequest filter) {
        Uni<Map<String, User>> all = this.findAll();

        String startsFrom = filter.getStartFrom();
        int offset = filter.getOffset();
        int limit = filter.getLimit();

        if (startsFrom != null) {
            String startsFromLowerCase = startsFrom.toLowerCase();
            all = all.map(el -> el.entrySet().stream()
                    .filter(entry -> entry.getValue().getLogin().toLowerCase().startsWith(startsFromLowerCase))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }

        Uni<Long> total = all.map(stringUserMap -> (long) stringUserMap.size());

        Uni<Map<String, UserResponse>> collect = all.map(el -> el.entrySet().stream()
                .skip(offset).limit(limit)
                .map(entry -> Map.entry(entry.getKey(), UserResponse.from(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return Uni.combine().all().unis(collect, total).combinedWith((responseMap, totalNum) -> new ResponsePaginatedWrapper<>(responseMap,
                new Pagination(totalNum, offset, limit)));
    }

    @Override
    public Uni<List<User>> getUsersByGroup(Group user) {
        throw new RuntimeException("Not implemented yet");
    }
}
