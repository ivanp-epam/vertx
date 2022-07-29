package com.programm.vertx.handler.middleware;

import com.programm.vertx.auth.JwtAuthProvider;
import com.programm.vertx.exceptions.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.core.http.HttpServerRequest;
import io.vertx.mutiny.ext.auth.User;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTAuthHandlerTest {

    @Mock
    RoutingContext event;

    @Mock
    HttpServerRequest request;

    @Mock
    User user;

    @Mock
    private JwtAuthProvider jwtAuthProvider;

    private JWTAuthHandler handler;

    @BeforeEach
    protected void setUp() {
        handler = new JWTAuthHandler(jwtAuthProvider);
    }

    @Test
    void emptyAuthorizationHeaderThrowsUnauthorizedException() {
        when(event.request()).thenReturn(request);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            handler.accept(event);
        });
    }

    @Test
    void malformedAuthorizationHeaderThrowsUnauthorizedException() {
        when(event.request()).thenReturn(request);
        when(request.getHeader(any())).thenReturn("asdqwe");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            handler.accept(event);
        });
    }

    @Test
    void absentBearerThrowsExceptionPath() {
        when(event.request()).thenReturn(request);
        when(request.getHeader(any())).thenReturn("Bearer1 asdqwe");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            handler.accept(event);
        });
    }

    @Test
    void successPath() {
        when(event.request()).thenReturn(request);
        when(request.getHeader(any())).thenReturn("Bearer asdqwe");
        Uni<User> item = Uni.createFrom().item(user);
        UniAssertSubscriber<User> assertSubscriber = item.subscribe().withSubscriber(UniAssertSubscriber.create());
        when(jwtAuthProvider.checkJwtToken("asdqwe")).thenReturn(item);
        when(user.expired()).thenReturn(false);
        handler.accept(event);

        assertSubscriber.assertCompleted()
                .assertSubscribed()
                .assertTerminated();
        verify(event, atLeastOnce()).next();
    }

    @Test
    void expiredUserThrowsException() {
        when(event.request()).thenReturn(request);
        when(request.getHeader(any())).thenReturn("Bearer asdqwe");
        Uni<User> item = Uni.createFrom().item(user);
        UniAssertSubscriber<User> assertSubscriber = item.subscribe().withSubscriber(UniAssertSubscriber.create());
        when(jwtAuthProvider.checkJwtToken("asdqwe")).thenReturn(item);
        when(user.expired()).thenReturn(true);
        handler.accept(event);
        assertSubscriber.assertCompleted();
        verify(event, atLeastOnce()).fail(any(UnauthorizedException.class));
    }
}
