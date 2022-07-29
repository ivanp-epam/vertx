package com.programm.vertx.repository.pgclient;

import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.RepositoryManager;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.PreparedQuery;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UserGroupRepositoryTest {
    @Mock
    PgPool client;

    @Mock
    RepositoryManager manager;

    UserGroupRepository handler;

    @BeforeEach
    protected void setUp() {
        handler = new UserGroupRepository(client);
    }

    @Test
    void removeAllUsersFromGroup() {
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> execute = mock(Uni.class);
        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(execute);
        when(execute.replaceWithVoid()).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> removeAllUsersFromGroup = handler.removeAllUsersFromGroup("ololo");
        removeAllUsersFromGroup.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    @Test
    void removeUserFromGroup() {
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> execute = mock(Uni.class);
        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(execute);
        when(execute.replaceWithVoid()).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> removeUserFromGroup = handler.removeUserFromGroup("ololo", "pewpew");
        removeUserFromGroup.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    @Test
    void setUsersToGroup() {
        Uni<Object> execute = Uni.createFrom().nullItem();
        when(client.withTransaction(any())).thenReturn(execute);

        Uni<Void> setUsersToGroup = handler.setUsersToGroup("ololo", List.of("pewpew"));
        setUsersToGroup.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    @Test
    void addUsersToGroup() {
        Uni<Object> execute = Uni.createFrom().nullItem();
        when(client.withTransaction(any())).thenReturn(execute);

        Uni<Void> addUsersToGroup = handler.addUsersToGroup("ololo", List.of("pewpew"));
        addUsersToGroup.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted();
    }

    @Test
    void checkUserGroupFail() {
        Uni<RowSet<Row>> execute = mock(Uni.class);
        RowIterator<Row> row = mock(RowIterator.class);

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(execute);
        when(execute.map(any())).thenReturn(Uni.createFrom().item(row));
        when(row.hasNext()).thenReturn(false);

        Uni<Void> checkUserGroup = handler.checkUserGroup("ololo", "pewpew");
        checkUserGroup.subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(EntityNotFoundException.class);
    }

    @Test
    void checkUserGroupPassed() {
        Uni<RowSet<Row>> execute = mock(Uni.class);
        RowIterator<Row> row = mock(RowIterator.class);

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(execute);
        when(execute.map(any())).thenReturn(Uni.createFrom().item(row));
        when(row.hasNext()).thenReturn(true);

        Uni<Void> checkUserGroup = handler.checkUserGroup("ololo", "pewpew");
        checkUserGroup.subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();
    }
}