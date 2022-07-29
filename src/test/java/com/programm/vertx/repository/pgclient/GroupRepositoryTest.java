package com.programm.vertx.repository.pgclient;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.RepositoryManager;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiCollect;
import io.smallrye.mutiny.groups.MultiFlatten;
import io.smallrye.mutiny.groups.MultiOnItem;
import io.smallrye.mutiny.groups.UniOnItem;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.core.json.Json;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class GroupRepositoryTest {

    @Mock
    PgPool client;

    @Mock
    RepositoryManager manager;

    GroupRepository handler;

    @BeforeEach
    protected void setUp() {
        handler = new GroupRepository(client, manager);
    }

    @Test
    void getGroupsByUser() {
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> uniRowSet = mock(Uni.class);
        UniOnItem<RowSet<Row>> uniOnItem = mock(UniOnItem.class);
        Multi<Object> multi = mock(Multi.class);
        MultiCollect<Object> multiCollect = mock(MultiCollect.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(uniRowSet);
        when(uniRowSet.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transformToMulti(any())).thenReturn(multi);
        when(multi.map(any())).thenReturn(multi);
        when(multi.collect()).thenReturn(multiCollect);
        when(multiCollect.asList()).thenReturn(Uni.createFrom().item(new ArrayList<>()));

        Uni<List<Group>> groupsByUser = handler.getGroupsByUser(new User(UUID.randomUUID().toString()));
        var assertSubscriber = groupsByUser.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void findAllWithData() {
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        PreparedQuery<RowSet<Object>> query = mock(PreparedQuery.class);
        Uni<RowSet<Object>> rowSet = mock(Uni.class);
        UniOnItem<RowSet<Object>> uniOnItem = mock(UniOnItem.class);

        Multi<Object> multi = mock(Multi.class);


        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.mapping(any())).thenReturn(query);
        when(query.execute()).thenReturn(rowSet);
        when(rowSet.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transformToMulti(any())).thenReturn(Multi.createFrom().empty());

        Uni<Map<String, Group>> all = handler.findAll();
        var assertSubscriber = all.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void findPaginated() {
        PreparedQuery<RowSet<Row>> preparedQuery1 = mock(PreparedQuery.class);
        PreparedQuery<RowSet<Row>> preparedQuery2 = mock(PreparedQuery.class);
        Uni<RowSet<Row>> execute = mock(Uni.class);
        UniOnItem<RowSet<Row>> executeUniOnItem = mock(UniOnItem.class);
        Multi<Object> executeMulti = mock(Multi.class);
        MultiOnItem<Object> executeMultiOnItem = mock(MultiOnItem.class);
        MultiFlatten<Object, Object> executeMultiFlatten = mock(MultiFlatten.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery1).thenReturn(preparedQuery2);
        when(preparedQuery1.execute(any())).thenReturn(execute);

        when(execute.onItem()).thenReturn(executeUniOnItem);
        when(executeUniOnItem.transformToMulti(any())).thenReturn(executeMulti);
        when(executeMulti.onItem()).thenReturn(executeMultiOnItem);
        when(executeMultiOnItem.transformToUni(any())).thenReturn(executeMultiFlatten);
        when(executeMultiFlatten.merge()).thenReturn(executeMulti);
        when(executeMulti.map(any())).thenReturn(executeMulti);
        when(executeMulti.collect()).thenReturn(new MultiCollect(Multi.createFrom().empty()));

        Uni<RowSet<Row>> executeCnt = mock(Uni.class);
        Uni<Object> executeCntUni = mock(Uni.class);
        when(preparedQuery2.execute()).thenReturn(executeCnt);
        when(executeCnt.map(any())).thenReturn(executeCntUni);
        when(executeCntUni.map(any())).thenReturn(Uni.createFrom().item(0));

        var findPaginated = handler.findPaginated(1, 1);
        var assertSubscriber = findPaginated.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void getWithEmpty() {
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        var get = handler.get("asdqwe");
        var assertSubscriber = get.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertFailedWith(EntityNotFoundException.class);
    }

    @Test
    void getWithData() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(group.getId());
        when(row.getString("name")).thenReturn(group.getName());
        when(row.getArrayOfStrings("permissions")).thenReturn(new String[]{Permission.WRITE.toString()});

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);
        when(rowIterator.next()).thenReturn(row);

        var get = handler.get("asdqwe");
        var assertSubscriber = get.subscribe().withSubscriber(UniAssertSubscriber.create());
        var item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(group));
    }


    @Test
    void addWithOutData() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        var add = handler.add(group);
        var assertSubscriber = add.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted().assertItem(null);

    }

    @Test
    void addWithData() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(group.getId());
        when(row.getString("name")).thenReturn(group.getName());
        when(row.getArrayOfStrings("permissions")).thenReturn(new String[]{Permission.WRITE.toString()});
        when(rowIterator.next()).thenReturn(row);

        var add = handler.add(group);
        var assertSubscriber = add.subscribe().withSubscriber(UniAssertSubscriber.create());

        var item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(group));
    }

    @Test
    void delete() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");

        when(client.withConnection(any())).thenReturn(Uni.createFrom().nullItem());

        var delete = handler.delete(group);
        var assertSubscriber = delete.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }


    @Test
    void updateWithOutData() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");
        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        var update = handler.update(group);
        var assertSubscriber = update.subscribe().withSubscriber(UniAssertSubscriber.create());

        update.await().indefinitely();
        assertSubscriber.assertCompleted().assertItem(null);

    }

    @Test
    void updateWithData() {
        Group group = new Group();
        group.setId(UUID.randomUUID()).setPermissions(List.of(Permission.WRITE)).setName("ololo");

        PreparedQuery<RowSet<Row>> preparedQuery = mock(PreparedQuery.class);
        Uni<RowSet<Row>> rowSetUni = mock(Uni.class);
        RowIterator<Row> rowIterator = mock(RowIterator.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(group.getId());
        when(row.getString("name")).thenReturn(group.getName());
        when(row.getArrayOfStrings("permissions")).thenReturn(new String[]{Permission.WRITE.toString()});
        when(rowIterator.next()).thenReturn(row);

        var update = handler.update(group);
        var assertSubscriber = update.subscribe().withSubscriber(UniAssertSubscriber.create());

        var item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(group));
    }

}