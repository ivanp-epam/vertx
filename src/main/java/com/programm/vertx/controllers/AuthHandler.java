package com.programm.vertx.controllers;

import com.programm.vertx.auth.JwtAuthProvider;
import com.programm.vertx.repository.IAuthRepository;
import com.programm.vertx.request.AuthRequest;
import com.programm.vertx.response.AuthResponse;
import io.vertx.core.json.Json;
import io.vertx.mutiny.ext.web.RoutingContext;

public class AuthHandler {
    private final JwtAuthProvider jwtAuthProvider;
    private final IAuthRepository authRepository;

    public AuthHandler(JwtAuthProvider jwtAuthProvider, IAuthRepository authRepository) {
        this.authRepository = authRepository;
        this.jwtAuthProvider = jwtAuthProvider;
    }

    public void auth(RoutingContext ctx) {
        AuthRequest authRequest = Json.decodeValue(ctx.body().getDelegate().buffer(), AuthRequest.class);

        authRepository
                .checkAuth(authRequest.getLogin(), authRequest.getPassword())
                .replaceWith(() -> jwtAuthProvider.createJwtToken(authRequest))
                .map(AuthResponse::new)
                .subscribe().with(ctx::jsonAndForget, ctx::fail);
    }
}
