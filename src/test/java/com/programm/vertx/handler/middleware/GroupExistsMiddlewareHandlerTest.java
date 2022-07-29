package com.programm.vertx.handler.middleware;

import com.programm.vertx.entities.Group;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IGroupRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupExistsMiddlewareHandlerTest {

    @Mock
    RoutingContext event;

    @Mock
    IGroupRepository repository;

    GroupExistsMiddlewareHandler handler;

    @BeforeEach
    protected void setUp() {
        handler = new GroupExistsMiddlewareHandler(repository);
    }

    @Test
    void passThroughIfNoneGroupIdProvided() {
        // given + when
        handler.accept(event);

        // then
        verify(event, atLeastOnce()).next();
    }

    @Test
    void passThroughIfGroupFound() {
        // given
        Uni<Group> item = Uni.createFrom().item(new Group());
        UniAssertSubscriber<Group> assertSubscriber = item.subscribe().withSubscriber(UniAssertSubscriber.create());

        when(event.pathParam("groupId")).thenReturn("123");
        when(repository.get("123")).thenReturn(item);

        // when
        handler.accept(event);

        // then
        verify(event, atLeastOnce()).next();
        assertSubscriber.assertCompleted().assertSubscribed().assertTerminated();
    }

    @Test
    void failIfGroupNotFound() {
        // given
        EntityNotFoundException ex = new EntityNotFoundException();
        Uni<Group> item = Uni.createFrom().failure(ex);
        UniAssertSubscriber<Group> assertSubscriber = item.subscribe().withSubscriber(UniAssertSubscriber.create());

        when(event.pathParam("groupId")).thenReturn("123");
        when(repository.get("123")).thenReturn(item);

        // when
        handler.accept(event);

        // then
        verify(event, atLeastOnce()).fail(ex);
        assertSubscriber.assertFailedWith(EntityNotFoundException.class).assertTerminated();
    }

}
