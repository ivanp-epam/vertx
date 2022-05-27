package com.programm.vertx.repository;

import java.util.Map;

public interface IRepository<T> {
  Map<String, T> findAll();

  T find(String id);

  T add(T entity);

  boolean delete(T entity);

  T update(T entity);
}
