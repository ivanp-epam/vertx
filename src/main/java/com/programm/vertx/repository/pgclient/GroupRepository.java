package com.programm.vertx.repository.pgclient;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.entities.User;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupRepository {

    private final SqlClient client;
    private final String GET_GROUPS_BY_USER = "SELECT g.id as id, g.name as name, g.permissions as permissions " +
            "FROM groups g " +
            "INNER JOIN users_groups ug ON g.id=ug.groups_id " +
            "WHERE ug.users_id=$1";

    public GroupRepository(SqlClient client) {
        this.client = client;
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
        return client.preparedQuery(GET_GROUPS_BY_USER)
                .execute(Tuple.of(user.getId()))
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(this::rowToGroup)
                .collect()
                .asList();
    }

}
