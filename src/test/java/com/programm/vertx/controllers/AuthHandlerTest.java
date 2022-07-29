package com.programm.vertx.controllers;

import com.programm.vertx.auth.JwtAuthProvider;
import com.programm.vertx.exceptions.InvalidCredentialsException;
import com.programm.vertx.repository.IAuthRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.ext.web.impl.RequestBodyImpl;
import io.vertx.mutiny.ext.web.RequestBody;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {
    @Mock
    RoutingContext event;

    @Mock
    JwtAuthProvider jwtAuthProvider;

    @Mock
    IAuthRepository authRepository;

    AuthHandler handler;

    @BeforeEach
    protected void setUp() {
        handler = new AuthHandler(jwtAuthProvider, authRepository);
    }


    @Test
    void successAuth() {
        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{}"));

        when(authRepository.checkAuth(any(), any())).thenReturn(Uni.createFrom().voidItem());
        when(jwtAuthProvider.createJwtToken(any())).thenReturn("ololo");

        handler.auth(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void failedAuth() {
        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{}"));

        when(authRepository.checkAuth(any(), any())).thenReturn(Uni.createFrom().failure(new InvalidCredentialsException()));

        handler.auth(event);

        verify(event, atLeastOnce()).fail(any());
    }
}