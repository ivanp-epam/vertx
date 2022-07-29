package com.programm.vertx.repository.pgclient;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.exceptions.InvalidCredentialsException;
import com.programm.vertx.repository.RepositoryManager;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.*;
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
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    PgPool client;

    @Mock
    RepositoryManager manager;

    @Mock
    Uni<RowSet<Row>> rowSetUni;

    @Mock
    RowIterator<Row> rowIterator;

    @Mock
    PreparedQuery<RowSet<Row>> preparedQuery;

    UserRepository handler;

    @BeforeEach
    protected void setUp() {
        handler = new UserRepository(client, manager);
    }

    @Test
    void deleteSql() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(Uni.createFrom().nullItem());

        Uni<Void> delete = handler.delete(user);
        UniAssertSubscriber<Void> assertSubscriber = delete.subscribe().withSubscriber(UniAssertSubscriber.create());

        delete.await().indefinitely();

        assertSubscriber.assertCompleted();

        verify(preparedQuery, atLeastOnce()).execute(any());
    }

    @Test
    void updateSqlWithNull() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        Uni<User> update = handler.update(user);
        UniAssertSubscriber<User> assertSubscriber = update.subscribe().withSubscriber(UniAssertSubscriber.create());

        update.await().indefinitely();
        assertSubscriber.assertCompleted().assertItem(null);
    }

    @Test
    void updateSqlWithData() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(user.getId());
        when(row.getString("login")).thenReturn(user.getLogin());
        when(row.getString("password")).thenReturn(user.getPassword());
        when(row.getInteger("age")).thenReturn(user.getAge());
        when(rowIterator.next()).thenReturn(row);

        Uni<User> update = handler.update(user);
        UniAssertSubscriber<User> assertSubscriber = update.subscribe().withSubscriber(UniAssertSubscriber.create());

        update.await().indefinitely();
        User item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(user));
    }

    @Test
    void addSqlWithNull() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        Uni<User> add = handler.add(user);
        UniAssertSubscriber<User> assertSubscriber = add.subscribe().withSubscriber(UniAssertSubscriber.create());

        add.await().indefinitely();
        assertSubscriber.assertCompleted().assertItem(null);
    }


    @Test
    void addSqlWithData() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(user.getId());
        when(row.getString("login")).thenReturn(user.getLogin());
        when(row.getString("password")).thenReturn(user.getPassword());
        when(row.getInteger("age")).thenReturn(user.getAge());
        when(rowIterator.next()).thenReturn(row);

        Uni<User> add = handler.add(user);
        UniAssertSubscriber<User> assertSubscriber = add.subscribe().withSubscriber(UniAssertSubscriber.create());

        add.await().indefinitely();
        User item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(user));
    }

    @Test
    void getSqlWithNull() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(false);

        Uni<User> get = handler.get("asdqwe");
        UniAssertSubscriber<User> assertSubscriber = get.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertFailedWith(EntityNotFoundException.class);
    }


    @Test
    void getSqlWithData() {
        User user = new User();
        user.setId(UUID.randomUUID()).setLogin("Login").setPassword("Pass").setAge(123);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(rowSetUni);
        when(rowSetUni.map(any())).thenReturn(Uni.createFrom().item(rowIterator));
        when(rowIterator.hasNext()).thenReturn(true);

        Row row = mock(Row.class);
        when(row.getUUID("id")).thenReturn(user.getId());
        when(row.getString("login")).thenReturn(user.getLogin());
        when(row.getString("password")).thenReturn(user.getPassword());
        when(row.getInteger("age")).thenReturn(user.getAge());
        when(rowIterator.next()).thenReturn(row);

        Uni<User> get = handler.get("asdqwe");
        UniAssertSubscriber<User> assertSubscriber = get.subscribe().withSubscriber(UniAssertSubscriber.create());

        get.await().indefinitely();
        User item = assertSubscriber.assertCompleted().getItem();

        assertEquals(Json.encode(item), Json.encode(user));
    }

    @Test
    void findAllWithData() {

        PreparedQuery<RowSet<Object>> query = mock(PreparedQuery.class);
        RowSet<Object> rowSet = mock(RowSet.class);
        RowIterator<Object> userIterator = mock(RowIterator.class);

        when(rowSet.iterator()).thenReturn(userIterator);
        when(userIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(userIterator.next()).thenReturn(new User(UUID.randomUUID().toString()));

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.mapping(any())).thenReturn(query);
        when(query.execute()).thenReturn(Uni.createFrom().item(rowSet));

        Uni<Map<String, User>> all = handler.findAll();
        UniAssertSubscriber<Map<String, User>> assertSubscriber = all.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void findByIds() {
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

        Uni<List<User>> findByIds = handler.findByIds(List.of(UUID.randomUUID().toString()));
        UniAssertSubscriber<List<User>> assertSubscriber = findByIds.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void findByGroups() {
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

        Uni<List<User>> findByGroup = handler.getUsersByGroup(new Group(UUID.randomUUID().toString()));

        UniAssertSubscriber<List<User>> assertSubscriber = findByGroup.subscribe().withSubscriber(UniAssertSubscriber.create());

        assertSubscriber.assertCompleted();
    }

    @Test
    void checkAuthException() {
        Uni<RowSet<Row>> uniRowSet = mock(Uni.class);
        UniOnItem<RowSet<Row>> uniOnItem = mock(UniOnItem.class);

        Multi<Object> multi = mock(Multi.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(uniRowSet);
        when(uniRowSet.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transformToMulti(any())).thenReturn(multi);
        when(multi.map(any())).thenReturn(multi);
        when(multi.toUni()).thenReturn(Uni.createFrom().nullItem());

        Uni<Void> checkAuth = handler.checkAuth("ololo", "pewpew");

        UniAssertSubscriber<Void> assertSubscriber = checkAuth.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertFailedWith(InvalidCredentialsException.class);
    }

    @Test
    void checkAuthPassMisMatch() {
        Uni<RowSet<Row>> uniRowSet = mock(Uni.class);
        UniOnItem<RowSet<Row>> uniOnItem = mock(UniOnItem.class);
        UniOnItem<Object> uniOnItemObj = mock(UniOnItem.class);
        UniOnNull<Object> uniOnNull = mock(UniOnNull.class);

        Multi<Object> multi = mock(Multi.class);
        Uni<Object> uni = mock(Uni.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(uniRowSet);
        when(uniRowSet.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transformToMulti(any())).thenReturn(multi);
        when(multi.map(any())).thenReturn(multi);
        when(multi.toUni()).thenReturn(uni);
        when(uni.onItem()).thenReturn(uniOnItemObj);
        when(uniOnItemObj.ifNull()).thenReturn(uniOnNull);
        when(uniOnNull.failWith(any(Supplier.class))).thenReturn(uni);
        when(uni.onItem()).thenReturn(uniOnItemObj);
        when(uniOnItemObj.transform(any())).thenReturn(Uni.createFrom().item(false));


        Uni<Void> checkAuth = handler.checkAuth("ololo", "pewpew");
        UniAssertSubscriber<Void> assertSubscriber = checkAuth.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertFailedWith(InvalidCredentialsException.class);
    }

    @Test
    void checkAuthPassSuccess() {
        Uni<RowSet<Row>> uniRowSet = mock(Uni.class);
        UniOnItem<RowSet<Row>> uniOnItem = mock(UniOnItem.class);
        UniOnItem<Object> uniOnItemObj = mock(UniOnItem.class);
        UniOnNull<Object> uniOnNull = mock(UniOnNull.class);

        Multi<Object> multi = mock(Multi.class);
        Uni<Object> uni = mock(Uni.class);

        when(client.preparedQuery(any())).thenReturn(preparedQuery);
        when(preparedQuery.execute(any())).thenReturn(uniRowSet);
        when(uniRowSet.onItem()).thenReturn(uniOnItem);
        when(uniOnItem.transformToMulti(any())).thenReturn(multi);
        when(multi.map(any())).thenReturn(multi);
        when(multi.toUni()).thenReturn(uni);
        when(uni.onItem()).thenReturn(uniOnItemObj);
        when(uniOnItemObj.ifNull()).thenReturn(uniOnNull);
        when(uniOnNull.failWith(any(Supplier.class))).thenReturn(uni);
        when(uni.onItem()).thenReturn(uniOnItemObj);
        when(uniOnItemObj.transform(any())).thenReturn(Uni.createFrom().item(true));


        Uni<Void> checkAuth = handler.checkAuth("ololo", "pewpew");
        UniAssertSubscriber<Void> assertSubscriber = checkAuth.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertCompleted();
    }

    @Test
    void findByPrefix() {
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
        when(preparedQuery2.execute(any())).thenReturn(executeCnt);
        when(executeCnt.map(any())).thenReturn(executeCntUni);
        when(executeCntUni.map(any())).thenReturn(Uni.createFrom().item(0));


        UsersFilterRequest usersFilterRequest = new UsersFilterRequest("asd", "1", "2");

        Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> byPrefix = handler.findByPrefix(usersFilterRequest);

        var assertSubscriber = byPrefix.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertCompleted();
    }

    @Test
    void findByNullPrefix() {
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

        UsersFilterRequest usersFilterRequest = new UsersFilterRequest(null, "1", "2");

        Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> byPrefix = handler.findByPrefix(usersFilterRequest);

        var assertSubscriber = byPrefix.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertCompleted();
    }

    @Test
    void findByEmptyPrefix() {
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


        UsersFilterRequest usersFilterRequest = new UsersFilterRequest("", "1", "2");

        Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> byPrefix = handler.findByPrefix(usersFilterRequest);

        var assertSubscriber = byPrefix.subscribe().withSubscriber(UniAssertSubscriber.create());
        assertSubscriber.assertCompleted();
    }
}