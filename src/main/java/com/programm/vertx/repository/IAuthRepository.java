package com.programm.vertx.repository;

import com.programm.vertx.entities.User;
import io.smallrye.mutiny.Uni;

public interface IAuthRepository {
    Uni<Void> checkAuth(String login, String password);
}
