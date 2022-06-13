package com.programm.vertx.repository;

import com.programm.vertx.exceptions.EntityNotFoundException;

import java.util.Map;

public interface IRepository<T> {
    Map<String, T> findAll();

    T find(String id);

    T get(String id) throws EntityNotFoundException;

    T add(T entity);

    boolean delete(T entity);

    T update(T entity);
}
