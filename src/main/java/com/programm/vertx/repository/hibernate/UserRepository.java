package com.programm.vertx.repository.hibernate;

import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponseWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserRepository2 {

    private final Mutiny.SessionFactory sessionFactory;

    public UserRepository2(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<Map<String, User>> findAll() {
        return sessionFactory
                .withSession(session -> session.createQuery("from User", User.class).getResultList())
                .onItem().transform(users -> users.stream().collect(
                        Collectors.toMap(User::getId, Function.identity())
                ));
    }

    public Uni<User> find(String id) {
        return sessionFactory
                .withSession(session -> session.find(User.class, id));
    }

    public Uni<User> get(String id) {
        return find(id).onItem().ifNull().failWith(new EntityNotFoundException());
    }

    public Uni<User> add(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .persist(entity)
                        .call(session::flush)
                        .replaceWith(entity));
    }

    public Uni<Void> delete(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .remove(entity)
                        .call(session::persist));
    }

    public Uni<User> update(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .persist(entity)
                        .call(session::persist)
                        .replaceWith(entity));
    }

    public Uni<ResponseWrapper<Map<String, UserResponse>>> findByPrefix(UsersFilterRequest filter) {

        Uni<Map<String, UserResponse>> userResponse = sessionFactory.withSession(session -> session
                        .createQuery("from User where login like :login%", User.class)

                        .setParameter("login", filter.getStartFrom())
                        .setFirstResult(filter.getOffset())
                        .setMaxResults(filter.getLimit())

                        .getResultList())
                .map(users -> users
                        .stream()
                        .map(UserResponse::from)
                        .collect(
                                Collectors.toMap(UserResponse::getId, Function.identity())
                        ));

        Uni<Integer> total = sessionFactory
                .withSession(session -> session
                        .createQuery("select count(*) from User where login like :login%", Integer.class)

                        .setParameter("login", filter.getStartFrom())

                        .getSingleResult());

        return Uni.combine()
                .all().unis(userResponse, total).combinedWith(
                        (responseMap, i) -> new ResponseWrapper<>(responseMap,
                                new Pagination(i, filter.getOffset(), filter.getLimit())));
    }
}
