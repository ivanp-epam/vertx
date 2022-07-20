package com.programm.vertx.auth;

import com.programm.vertx.exceptions.UnauthorizedException;
import com.programm.vertx.request.AuthRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.auth.User;
import io.vertx.mutiny.ext.auth.jwt.JWTAuth;

public class JwtAuthProvider {

    private final JWTAuth jwtAuth;

    public JwtAuthProvider(JWTAuth jwtAuth) {
        this.jwtAuth = jwtAuth;
    }

    public String createJwtToken(AuthRequest authRequest) {
        return jwtAuth.generateToken(
                new JsonObject()
                        .put("sub", authRequest.getLogin())
                        .put("name", authRequest.getLogin())
        );
    }

    public Uni<User> checkJwtToken(String token) {
        return jwtAuth.authenticate(new JsonObject().put("token", token))
                .onFailure()
                .transform(throwable -> new UnauthorizedException());
    }
}
