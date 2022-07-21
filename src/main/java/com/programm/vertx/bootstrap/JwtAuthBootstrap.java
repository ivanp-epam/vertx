package com.programm.vertx.bootstrap;

import com.programm.vertx.auth.JwtAuthProvider;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.auth.jwt.JWTAuth;

public class JwtAuthBootstrap {

    private final Vertx vertx;

    public JwtAuthBootstrap(Vertx vertx) {
        this.vertx = vertx;
    }

    public JWTAuth jwtAuth() {
        JWTAuthOptions config = new JWTAuthOptions()
                .setKeyStore(new KeyStoreOptions()
                        .setPath("test.keystore")
                        .setPassword("123456"));

        return JWTAuth.create(vertx, config);
    }

    public JwtAuthProvider jwtAuthProvider() {
        return new JwtAuthProvider(jwtAuth());
    }
}
