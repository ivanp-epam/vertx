package com.programm.vertx.repository.pgclient;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.exceptions.InvalidCredentialsException;
import com.programm.vertx.repository.IAuthRepository;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.repository.RepositoryManager;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class UserRepository implements IUserRepository, IAuthRepository {

    private final PgPool client;
    private final RepositoryManager manager;

    private final String GET_USERS_BY_GROUP = "SELECT u.id as id, u.login as login, u.password as password, u.age as age " +
            "FROM users u " +
            "INNER JOIN users_groups ug ON u.id=ug.users_id " +
            "WHERE is_deleted = false AND ug.groups_id=$1";

    private final String FIND_BY_IDS_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false AND id = any ($1)";

    private final String FIND_ALL_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false";
    private final String FIND_BY_ID_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false AND id = $1";

    private final String FIND_WITH_PREFIX_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false AND login ilike $1 limit $2 offset $3";
    private final String FIND_WITHOUT_PREFIX_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false limit $1 offset $2";

    private final String FIND_WITH_PREFIX_COUNT_SQL = "SELECT count(*) as count FROM users WHERE is_deleted = false AND login ilike $1";
    private final String FIND_WITHOUT_PREFIX_COUNT_SQL = "SELECT count(*) as count FROM users WHERE is_deleted = false";

    private final String ADD_SQL = "INSERT INTO users(id, login, password, age) values ($1, $2, $3, $4) returning id, login, password, age";
    private final String UPDATE_SQL = "UPDATE users SET login=$2, password=$3, age=$4 WHERE id=$1 returning id, login, password, age";
    private final String DELETE_SQL = "UPDATE users SET is_deleted=true WHERE id=$1 and is_deleted=false";

    private final String FIND_USER_BY_LOGIN_SQL = "SELECT id, login, password, age FROM users WHERE is_deleted = false AND login = $1 limit 1";

    public UserRepository(PgPool client, RepositoryManager manager) {
        this.client = client;
        this.manager = manager;
    }

    private User mapToUser(Row row) {
        return new User()
                .setId(row.getUUID("id"))
                .setLogin(row.getString("login"))
                .setPassword(row.getString("password"))
                .setAge(row.getInteger("age"));
    }

    public Uni<List<User>> getUsersByGroup(Group group) {
        return client.preparedQuery(GET_USERS_BY_GROUP)
                .execute(Tuple.of(group.getId()))
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::mapToUser)
                .collect()
                .asList();
    }

    @Override
    public Uni<Map<String, User>> findAll() {
        return client.preparedQuery(FIND_ALL_SQL).mapping(this::mapToUser)
                .execute().map(users -> {
                    RowIterator<User> iterator = users.iterator();
                    Map<String, User> map = new HashMap<>();
                    while (iterator.hasNext()) {
                        User u = iterator.next();
                        map.put(u.getId().toString(), u);
                    }
                    return map;
                });
    }

    @Override
    public Uni<User> find(String id) {
        return client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(id))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? mapToUser(it.next()) : null);
    }

    @Override
    public Uni<List<User>> findByIds(List<String> ids) {
        return client.preparedQuery(FIND_BY_IDS_SQL)
                .execute(Tuple.of(ids.stream().map(UUID::fromString).toArray(UUID[]::new)))
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::mapToUser)
                .collect()
                .asList();
    }

    @Override
    public Uni<User> get(String id) throws EntityNotFoundException {
        return this.find(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @Override
    public Uni<Void> checkAuth(String login, String password) {
        return client.preparedQuery(FIND_USER_BY_LOGIN_SQL)
                .execute(Tuple.of(login))
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::mapToUser)
                .toUni()
                .onItem()
                .ifNull()
                .failWith(InvalidCredentialsException::new)
                .onItem()
                .transform(user -> password.equals(user.getPassword()))
                .map(Unchecked.function(boolVal -> {
                    if (boolVal) {
                        return true;
                    }
                    throw new InvalidCredentialsException();
                }))
                .replaceWithVoid();

    }

    @Override
    public Uni<User> add(User entity) {
        return client.preparedQuery(ADD_SQL)
                .execute(Tuple.of(entity.getId(), entity.getLogin(), entity.getPassword(), entity.getAge()))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? mapToUser(it.next()) : null);
    }

    @Override
    public Uni<Void> delete(User entity) {
        return client.preparedQuery(DELETE_SQL)
                .execute(Tuple.of(entity.getId()))
                .replaceWithVoid();
    }

    @Override
    public Uni<User> update(User entity) {
        return client.preparedQuery(UPDATE_SQL)
                .execute(Tuple.of(entity.getId(), entity.getLogin(), entity.getPassword(), entity.getAge()))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? mapToUser(it.next()) : null);
    }

    @Override
    public Uni<ResponsePaginatedWrapper<Map<String, UserResponse>>> findByPrefix(UsersFilterRequest filter) {

        Uni<RowSet<Row>> execute;
        Uni<RowSet<Row>> executeCnt;

        if (filter.getStartFrom() == null || filter.getStartFrom().isEmpty()) {
            execute = client.preparedQuery(FIND_WITHOUT_PREFIX_SQL)
                    .execute(Tuple.of(filter.getLimit(), filter.getOffset()));
            executeCnt = client.preparedQuery(FIND_WITHOUT_PREFIX_COUNT_SQL)
                    .execute();
        } else {
            String startFrom = filter.getStartFrom() + "%";
            execute = client.preparedQuery(FIND_WITH_PREFIX_SQL)
                    .execute(Tuple.of(startFrom, filter.getLimit(), filter.getOffset()));
            executeCnt = client.preparedQuery(FIND_WITH_PREFIX_COUNT_SQL)
                    .execute(Tuple.of(startFrom));
        }
        Uni<Map<String, UserResponse>> mapUni = execute
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::mapToUser)
                .onItem()
                .transformToUni(userResponse -> manager.getGroupRepository().getGroupsByUser(userResponse).map(userResponse::setGroups))
                .merge()
                .map(UserResponse::from)
                .collect()
                .asMap(UserResponse::getId, Function.identity());

        Uni<Integer> count = executeCnt
                .map(RowSet::iterator)
                .map(iterator -> iterator.next().getInteger("count"));

        return Uni.combine().all().unis(mapUni, count)
                .combinedWith((responseMap, i) -> new ResponsePaginatedWrapper<>(responseMap,
                        new Pagination(i, filter.getOffset(), filter.getLimit())));
    }
}
