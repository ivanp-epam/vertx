package com.programm.vertx.repository.pgclient;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IGroupRepository;
import com.programm.vertx.repository.RepositoryManager;
import com.programm.vertx.response.GroupResponse;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupRepository implements IGroupRepository {

    private final PgPool connection;
    private final RepositoryManager manager;

    private final String GET_GROUPS_BY_USER = "SELECT g.id as id, g.name as name, g.permissions as permissions " +
            "FROM groups g " +
            "INNER JOIN users_groups ug ON g.id=ug.groups_id " +
            "WHERE ug.users_id=$1";

    private final String FIND_ALL_SQL = "SELECT id, name, permissions FROM groups";

    private final String FIND_ALL_LIMIT_OFFSET_SQL = "SELECT id, name, permissions FROM groups limit $1 offset $2";
    private final String FIND_ALL_COUNT_SQL = "SELECT count(*) as count FROM groups";

    private final String FIND_BY_ID_SQL = "SELECT id, name, permissions FROM groups WHERE id = $1";

    private final String ADD_SQL = "INSERT INTO groups(name, permissions) values ($1, $2) returning id, name, permissions";
    private final String UPDATE_SQL = "UPDATE groups SET name=$2, permissions=$3 WHERE id=$1 returning id, name, permissions";
    private final String DELETE_SQL = "DELETE FROM groups WHERE id=$1";
    private final String DELETE_CONNECTED_ROWS_SQL = "DELETE FROM users_groups WHERE groups_id=$1";


    public GroupRepository(PgPool pool, RepositoryManager manager) {
        this.connection = pool;
        this.manager = manager;
    }

    private Group rowToGroup(Row row) {
        return new Group()
                .setId(row.getUUID("id"))
                .setName(row.getString("name"))
                .setPermissions(
                        Arrays
                                .stream(row.getArrayOfStrings("permissions"))
                                .map(Permission::valueOf)
                                .collect(Collectors.toList())
                );
    }

    public Uni<List<Group>> getGroupsByUser(User user) {
        return connection.preparedQuery(GET_GROUPS_BY_USER)
                .execute(Tuple.of(user.getId()))
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::rowToGroup)
                .collect()
                .asList();
    }

    @Override
    public Uni<Map<String, Group>> findAll() {
        return connection.preparedQuery(FIND_ALL_SQL)
                .mapping(this::rowToGroup)
                .execute()
                .onItem().transformToMulti(groups -> Multi.createFrom().iterable(groups))
                .collect().asMap(Group::getIdAsString, Function.identity());
    }

    public Uni<ResponsePaginatedWrapper<Map<String, GroupResponse>>> findPaginated(int limit, int offset) {

        return Uni.combine().all().unis(
                connection.preparedQuery(FIND_ALL_LIMIT_OFFSET_SQL)
                        .execute(Tuple.of(limit, offset))
                        .onItem()
                        .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                        .map(this::rowToGroup)
                        .onItem()
                        .transformToUni(groupResponse -> manager.getUserRepository().getUsersByGroup(groupResponse).map(groupResponse::setUsers))
                        .merge()
                        .map(GroupResponse::from)
                        .collect().asMap(GroupResponse::getId, Function.identity()),

                connection.preparedQuery(FIND_ALL_COUNT_SQL)
                        .execute().map(RowSet::iterator)
                        .map(iterator -> iterator.next().getInteger("count"))
        ).combinedWith((responseMap, i) -> new ResponsePaginatedWrapper<>(responseMap,
                new Pagination(i, offset, limit)));


    }

    @Override
    public Uni<Group> find(String id) {
        return connection.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(id))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? rowToGroup(it.next()) : null);
    }

    @Override
    public Uni<Group> get(String id) throws EntityNotFoundException {
        return this.find(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @Override
    public Uni<Group> add(Group entity) {
        return connection.preparedQuery(ADD_SQL)
                .execute(Tuple.of(entity.getName(), entity.getPermissionsAsStrings()))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? rowToGroup(it.next()) : null);
    }

    @Override
    public Uni<Void> delete(Group entity) {
        return connection.withConnection(conn -> {
            Uni<RowSet<Row>> delete1 = conn.preparedQuery(DELETE_CONNECTED_ROWS_SQL).execute(Tuple.of(entity.getId()));
            Uni<RowSet<Row>> delete2 = conn.preparedQuery(DELETE_SQL).execute(Tuple.of(entity.getId()));
            return Uni.combine().all().unis(delete1, delete2)
                    .discardItems();
        });
    }

    @Override
    public Uni<Group> update(Group entity) {
        return connection.preparedQuery(UPDATE_SQL)
                .execute(Tuple.of(entity.getId(), entity.getName(), entity.getPermissionsAsStrings()))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? rowToGroup(it.next()) : null);
    }
}
