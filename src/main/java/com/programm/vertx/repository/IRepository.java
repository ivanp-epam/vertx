package com.programm.vertx.repository;

import com.programm.vertx.exceptions.EntityNotFoundException;
import io.smallrye.mutiny.Uni;

import java.util.Map;

public interface IRepository<T> {
    Uni<Map<String, T>> findAll();

    Uni<T> find(String id);

    Uni<T> get(String id) throws EntityNotFoundException;

    Uni<T> add(T entity);

    Uni<Void> delete(T entity);

    Uni<T> update(T entity);
}
