package com.programm.vertx.handler.middleware;

import com.programm.vertx.auth.JwtAuthProvider;
import com.programm.vertx.exceptions.UnauthorizedException;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

public class JWTAuthHandler implements Consumer<RoutingContext> {
    private final JwtAuthProvider jwtAuthProvider;

    public JWTAuthHandler(JwtAuthProvider jwtAuthProvider) {
        this.jwtAuthProvider = jwtAuthProvider;
    }

    public void handle(RoutingContext ctx) {
        String authorization = ctx.request().getHeader("Authorization");
        if (authorization == null) {
            throw new UnauthorizedException();
        }

        String[] bearerHeaders = authorization.split(" ");
        if (bearerHeaders.length != 2) {
            throw new UnauthorizedException();
        }

        if (!bearerHeaders[0].equals("Bearer")) {
            throw new UnauthorizedException();
        }
        String token = bearerHeaders[1];

        jwtAuthProvider.checkJwtToken(token)
                .onFailure().invoke(ctx::fail)
                .map(Unchecked.function(user -> {
                    if (user.expired()) {
                        throw new UnauthorizedException();
                    }
                    return user;
                }))
                .onFailure().invoke(ctx::fail)
                .subscribe().with((unused) -> ctx.next());

    }

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }

}
