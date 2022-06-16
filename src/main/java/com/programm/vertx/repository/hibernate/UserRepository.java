package com.programm.vertx.repository.hibernate;

import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponseWrapper;
import com.programm.vertx.response.UserResponse;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public UserRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Map<String, User> findAll() {
        List<Map<String, User>> u = new ArrayList<>();

        sessionFactory
                .withSession(session -> session.createQuery("from User", User.class).getResultList())
                .onItem().transform(users -> users.stream().collect(
                        Collectors.toMap(User::getId, Function.identity())
                )).subscribe().with(u::add);

        return u.get(0);
    }

    @Override
    public User find(String id) {
        List<User> u = new ArrayList<>();
        sessionFactory
                .withSession(session -> session.find(User.class, id))
                .subscribe().with(u::add);
        return u.get(0);
    }

    @Override
    public User get(String id) throws EntityNotFoundException {
        User user = find(id);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        return user;
    }

    @Override
    public User add(User entity) {
        sessionFactory
                .withTransaction((session) -> session
                        .persist(entity)
                        .call(session::flush))
                .subscribe().with(unused -> new Integer(1));
        return entity;
    }

    @Override
    public boolean delete(User entity) {
        sessionFactory
                .withTransaction((session) -> session
                        .remove(entity)
                        .call(session::persist))
                .subscribe().with(unused -> new Integer(1));
        return true;
    }

    @Override
    public User update(User entity) {
        List<User> u = new ArrayList<>();
        sessionFactory
                .withTransaction((session) -> session
                        .persist(entity)
                        .call(session::persist)
                        .replaceWith(entity))
                .subscribe().with(u::add);

        return u.get(0);
    }

    @Override
    public ResponseWrapper<Map<String, UserResponse>> findByPrefix(UsersFilterRequest filter) {
        List<Map<String, UserResponse>> userResponse = new ArrayList<>();

        sessionFactory.withSession(session -> {
                    return session
                            .createQuery("from User where login like :login%", User.class)
                            .setParameter("login", filter.getStartFrom())
                            .setFirstResult(filter.getOffset())
                            .setMaxResults(filter.getLimit())
                            .getResultList();

                })
                .onItem().transform(users -> users
                        .stream()
                        .map(UserResponse::from)
                        .collect(
                                Collectors.toMap(UserResponse::getId, Function.identity())
                        ))
                .subscribe().with(userResponse::add);

        List<Integer> total = new ArrayList<>();
        sessionFactory
                .withSession(session -> session
                        .createQuery("select count(*) from User where login like :login%", Integer.class)
                        .setParameter("login", filter.getStartFrom())
                        .getSingleResult()).subscribe().with(total::add);

        return new ResponseWrapper<>(userResponse.get(0),
                new Pagination(total.get(0), filter.getOffset(), filter.getLimit()));
    }
}
