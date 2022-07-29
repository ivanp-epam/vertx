package com.programm.vertx.controllers;

import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.exceptions.ValidationException;
import com.programm.vertx.repository.IUserGroupRepository;
import com.programm.vertx.repository.IUserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGroupHandlerTest {
    @Mock
    RoutingContext event;

    @Mock
    IUserRepository userRepository;

    @Mock
    IUserGroupRepository userGroupRepository;

    @Mock
    HttpServerRequest request;

    UserGroupHandler handler;

    @BeforeEach
    protected void setUp() {
        handler = new UserGroupHandler(userGroupRepository, userRepository);
    }

    @Test
    void getUsersFailed() {
        when(event.request()).thenReturn(request);
        when(request.getParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userRepository.getUsersByGroup(any())).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));

        handler.getUsers(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void getUsersSucceed() {
        User user = new User();
        user.setAge(100).setId(UUID.randomUUID()).setPassword("ololo").setLogin("pewpew");
        List<User> userList = List.of(user);

        when(event.request()).thenReturn(request);
        when(request.getParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userRepository.getUsersByGroup(any())).thenReturn(Uni.createFrom().item(userList));

        handler.getUsers(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void getUserFailed() {
        User user = new User();
        user.setAge(100).setId(UUID.randomUUID()).setPassword("ololo").setLogin("pewpew");

        when(event.request()).thenReturn(request);
        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(request.getParam(eq("userId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.checkUserGroup(any(String.class), any(String.class))).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));

        handler.getUser(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void getUserSucceed() {
        User user = new User();
        user.setAge(100).setId(UUID.randomUUID()).setPassword("ololo").setLogin("pewpew");

        when(event.request()).thenReturn(request);
        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(request.getParam(eq("userId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.checkUserGroup(any(String.class), any(String.class))).thenReturn(Uni.createFrom().voidItem());
        when(userRepository.get(any(String.class))).thenReturn(Uni.createFrom().item(user));

        handler.getUser(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void updateUsersFailed() {
        User user = new User();
        user.setAge(100).setId(UUID.randomUUID()).setPassword("ololo").setLogin("pewpew");
        List<User> userList = List.of(user);

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"userIds\":[\"6543be9e-5e19-4c98-aa2e-05ce13056cca\",\"fbdd6553-e7aa-4e73-8b23-eacf4d22f6b8\"]}"));


        when(event.request()).thenReturn(request);
        when(request.getParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userRepository.findByIds(any())).thenReturn(Uni.createFrom().item(userList));

        handler.updateUsers(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void updateUsersSucceed() {
        User user = new User();
        user.setAge(100).setId(UUID.randomUUID()).setPassword("ololo").setLogin("pewpew");
        List<User> userList = List.of(user, user);

        RequestBody body = mock(RequestBody.class);
        RequestBodyImpl impl = mock(RequestBodyImpl.class);
        when(event.body()).thenReturn(body);
        when(body.getDelegate()).thenReturn(impl);
        when(impl.buffer()).thenReturn(BufferImpl.buffer("{\"userIds\":[\"6543be9e-5e19-4c98-aa2e-05ce13056cca\",\"fbdd6553-e7aa-4e73-8b23-eacf4d22f6b8\"]}"));


        when(event.request()).thenReturn(request);
        when(request.getParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userRepository.findByIds(any())).thenReturn(Uni.createFrom().item(userList));
        when(userGroupRepository.setUsersToGroup(any(), any())).thenReturn(Uni.createFrom().voidItem());
        when(userRepository.getUsersByGroup(any())).thenReturn(Uni.createFrom().item(userList));

        handler.updateUsers(event);

        verify(event, atLeastOnce()).jsonAndForget(any());
    }

    @Test
    void deleteUsersFromGroupFailed() {
        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.removeAllUsersFromGroup(any())).thenReturn(Uni.createFrom().failure(new ValidationException(new ArrayList<>())));

        handler.deleteUsersFromGroup(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void deleteUsersFromGroupSucceed() {
        HttpServerResponse response = mock(HttpServerResponse.class);

        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.removeAllUsersFromGroup(any())).thenReturn(Uni.createFrom().voidItem());
        when(event.response()).thenReturn(response);
        when(response.setStatusCode(any(Integer.class))).thenReturn(response);

        handler.deleteUsersFromGroup(event);

        verify(response, atLeastOnce()).endAndForget();
    }

    @Test
    void deleteParticularUsersFromGroupFailed() {
        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(event.pathParam(eq("userId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.checkUserGroup(any(), any())).thenReturn(Uni.createFrom().failure(new EntityNotFoundException()));

        handler.deleteParticularUsersFromGroup(event);

        verify(event, atLeastOnce()).fail(any());
    }

    @Test
    void deleteParticularUsersFromGroupSucceed() {
        HttpServerResponse response = mock(HttpServerResponse.class);

        when(event.pathParam(eq("groupId"))).thenReturn(UUID.randomUUID().toString());
        when(event.pathParam(eq("userId"))).thenReturn(UUID.randomUUID().toString());
        when(userGroupRepository.checkUserGroup(any(), any())).thenReturn(Uni.createFrom().voidItem());
        when(userGroupRepository.removeUserFromGroup(any(), any())).thenReturn(Uni.createFrom().voidItem());
        when(event.response()).thenReturn(response);
        when(response.setStatusCode(any(Integer.class))).thenReturn(response);

        handler.deleteParticularUsersFromGroup(event);

        verify(response, atLeastOnce()).endAndForget();
    }
}