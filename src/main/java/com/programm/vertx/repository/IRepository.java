package com.programm.vertx.repository;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

public interface IRepository<T> {
    Uni<Map<String, T>> findAll();

    Uni<T> find(String id);

    Uni<T> get(String id) throws EntityNotFoundException;

    Uni<T> add(T entity);

    Uni<Void> delete(T entity);

    Uni<T> update(T entity);
}
