package com.programm.vertx.controllers;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.exceptions.MalformedJsonException;
import com.programm.vertx.repository.IGroupRepository;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.ext.web.impl.RequestBodyImpl;
import io.vertx.mutiny.core.http.HttpServerRequest;
import io.vertx.mutiny.core.http.HttpServerResponse;
import io.vertx.mutiny.ext.web.RequestBody;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupsHandlerTest {
    @Mock
    RoutingContext event;

    @Mock
    IGroupRepository repository;

    @Mock
    HttpServerRequest request;

    GroupsHandler handler;

    @BeforeEach
    protected void setUp() {
        handler = new GroupsHandler(repository);
    }

    @Test
    void successGetAll() {

        when(event.request()).thenReturn(request);
        when(request.getParam(eq("limit"), any())).thenReturn("10");
        when(request.getParam(eq("offset"), any())).thenReturn("0");
        when(repository.findPaginated(eq(10), eq(0))).thenReturn(
                Uni.createFrom().item(
                        new ResponsePaginatedWrapper<>(new HashMap<>(), new Pagination(10, 0, 0))
                )
        );

        handler.getAll(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void failedGetAll() {

        when(event.request()).thenReturn(request);
        when(request.getParam(eq("limit"), any())).thenReturn("10");
        when(request.getParam(eq("offset"), any())).thenReturn("0");
        when(repository.findPaginated(eq(10), eq(0))).thenReturn(
                Uni.createFrom().failure(new MalformedJsonException())
        );

        handler.getAll(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void createSuccess() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"name\":\"a\", \"permissions\":[\"WRITE\"]}"));

        when(repository.add(any())).thenReturn(Uni.createFrom().item(group));

        handler.create(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void createFailed() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"name\":\"a\", \"permissions\":[\"WRITE\"]}"));

        when(repository.add(any())).thenReturn(Uni.createFrom().failure(new RuntimeException()));

        handler.create(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void getFailed() {
        when(event.pathParam(eq("id"))).thenReturn("ololo");
        when(repository.get(any())).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));
        handler.get(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void getSucceed() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));
        when(event.pathParam(eq("id"))).thenReturn("ololo");

        when(repository.get(any())).thenReturn(Uni.createFrom().item(group));
        handler.get(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void putSucceed() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"name\":\"a\", \"permissions\":[\"WRITE\"]}"));

        when(repository.get(any())).thenReturn(Uni.createFrom().item(group));
        when(event.pathParam(eq("id"))).thenReturn("ololo");
        when(repository.update(any())).thenReturn(Uni.createFrom().item(group));

        handler.put(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
        verify(repository, atLeastOnce()).update(any());
    }

    @Test
    void putFailed() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"name\":\"a\", \"permissions\":[\"WRITE\"]}"));

        when(repository.get(any())).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));

        handler.put(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void deleteFailed() {
        when(event.pathParam(eq("id"))).thenReturn("ololo");

        when(repository.get(any())).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));

        handler.delete(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void deleteSucceed() {
        Group group = new Group();
        group.setName("a").setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE));
        HttpServerResponse response = mock(HttpServerResponse.class);

        when(event.pathParam(eq("id"))).thenReturn("ololo");
        when(event.response()).thenReturn(response);
        when(response.setStatusCode(any(Integer.class))).thenReturn(response);

        when(repository.get(eq("ololo"))).thenReturn(Uni.createFrom().item(group));
        when(repository.delete(any())).thenReturn(Uni.createFrom().voidItem());

        handler.delete(event);

        verify(response, atLeastOnce()).endAndForget();
    }

}