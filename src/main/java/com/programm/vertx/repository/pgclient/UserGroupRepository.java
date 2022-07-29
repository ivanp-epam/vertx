package com.programm.vertx.repository.pgclient;

import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IUserGroupRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.List;

public class UserGroupRepository implements IUserGroupRepository {

    private static final String SQL_USER_GROUP_EXISTS = "SELECT 1 FROM users_groups WHERE groups_id=$1 AND users_id=$2 LIMIT 1";
    private static final String SQL_ADD = "INSERT INTO users_groups(groups_id, users_id) VALUES($1, $2)";
    private static final String SQL_REMOVE = "DELETE FROM users_groups WHERE groups_id = $1 AND users_id = $2";
    private static final String SQL_REMOVE_ALL = "DELETE FROM users_groups WHERE groups_id=$1";

    private final PgPool pool;

    public UserGroupRepository(PgPool pool) {
        this.pool = pool;
    }


    public Uni<Void> checkUserGroup(String groupId, String userId) {
        return pool.preparedQuery(SQL_USER_GROUP_EXISTS)
                .execute(Tuple.of(groupId, userId))
                .map(RowSet::iterator)
                .invoke(Unchecked.consumer(it -> {
                    if (!it.hasNext()) {
                        throw new EntityNotFoundException();
                    }
                })).replaceWithVoid();

    }

    public Uni<Void> addUsersToGroup(String groupId, List<String> userIds) {
        return pool.withTransaction(connection -> {

            List<Uni<RowSet<Row>>> unis = userIds.stream()
                    .map(userId -> connection
                            .preparedQuery(SQL_ADD)
                            .execute(Tuple.of(groupId, userId)))
                    .toList();

            return Uni
                    .combine()
                    .all()
                    .unis(unis)
                    .discardItems();

        });
    }

    public Uni<Void> removeUserFromGroup(String groupId, String userId) {
        return pool
                .preparedQuery(SQL_REMOVE)
                .execute(Tuple.of(groupId, userId))
                .replaceWithVoid();
    }

    public Uni<Void> removeAllUsersFromGroup(String groupId) {
        return pool.preparedQuery(SQL_REMOVE_ALL)
                .execute(Tuple.of(groupId))
                .replaceWithVoid();
    }

    public Uni<Void> setUsersToGroup(String groupId, List<String> userIds) {
        return pool.withTransaction(connection -> connection
                .preparedQuery(SQL_REMOVE_ALL)
                .execute(Tuple.of(groupId))
                .call(() -> this.addUsersToGroup(groupId, userIds))
                .replaceWithVoid());
    }
}
