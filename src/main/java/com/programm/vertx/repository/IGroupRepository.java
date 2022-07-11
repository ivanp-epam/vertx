package com.programm.vertx.repository;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.response.GroupResponse;
import com.programm.vertx.response.ResponsePaginatedWrapper;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Map;

public interface IGroupRepository extends IRepository<Group> {
    Uni<List<Group>> getGroupsByUser(User user);

    Uni<ResponsePaginatedWrapper<Map<String, GroupResponse>>> findPaginated(int limit, int offset);
}
