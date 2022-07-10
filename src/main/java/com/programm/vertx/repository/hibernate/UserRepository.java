package com.programm.vertx.repository.hibernate;

import com.programm.vertx.entities.Group;
import com.programm.vertx.entities.User;
import com.programm.vertx.exceptions.EntityNotFoundException;
import com.programm.vertx.repository.IUserRepository;
import com.programm.vertx.request.UsersFilterRequest;
import com.programm.vertx.response.Pagination;
import com.programm.vertx.response.ResponseWrapper;
import com.programm.vertx.response.UserResponse;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public UserRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<Map<String, User>> findAll() {
        return sessionFactory
                .withSession(session -> session.createQuery("from User", User.class).getResultList())
                .onItem().transform(users -> users.stream().collect(
                        Collectors.toMap(User::getStringId, Function.identity())
                ));
    }

    public Uni<User> find(String id) {
        return sessionFactory
                .withSession(session -> session.find(User.class, UUID.fromString(id)));
    }

    public Uni<User> get(String id) {
        return find(id).onItem().ifNull().failWith(new EntityNotFoundException());
    }

    public Uni<User> add(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .persist(entity)
                        .call(session::flush)
                        .replaceWith(entity));
    }

    public Uni<Void> delete(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .find(User.class, entity.getId())
                        .call(session::remove)
                        .call(session::flush)
                        .replaceWithVoid()
                );
    }

    public Uni<User> update(User entity) {
        return sessionFactory
                .withTransaction((session) -> session
                        .find(User.class, entity.getId())
                        .call((user) -> session.persist(
                                user.setAge(entity.getAge())
                                        .setLogin(entity.getLogin())
                                        .setPassword(entity.getPassword()))
                        )
//                        .persist(entity)
//                        .call(session::persist)
                        .call(session::flush)
                        .replaceWith(entity));
    }

    public Uni<ResponseWrapper<Map<String, UserResponse>>> findByPrefix(UsersFilterRequest filter) {

        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();

        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> a = query.from(User.class);

        CriteriaQuery<Long> queryCount = criteriaBuilder.createQuery(Long.class);
        Root<User> b = queryCount.from(User.class);

        queryCount = queryCount.select(criteriaBuilder.count(b));

        if (filter.getStartFrom() != null) {
            query = query.where(criteriaBuilder.like(a.get("login"), filter.getStartFrom() + "%"));
            queryCount = queryCount.where(criteriaBuilder.like(a.get("login"), filter.getStartFrom() + "%"));
        }

        CriteriaQuery<User> finalQuery = query;

        Uni<Map<String, UserResponse>> userResponse = sessionFactory.withSession(session -> session
                        .createQuery(finalQuery)
                        .setFirstResult(filter.getOffset())
                        .setMaxResults(filter.getLimit())

                        .getResultList())
                .map(users -> users
                        .stream()
                        .map(UserResponse::from)
                        .collect(
                                Collectors.toMap(UserResponse::getId, Function.identity())
                        ));

        CriteriaQuery<Long> finalQueryCount = queryCount;

        Uni<Long> total = sessionFactory
                .withSession(session -> session
                        .createQuery(finalQueryCount)
                        .getSingleResult());

        return Uni.combine()
                .all().unis(userResponse, total).combinedWith(
                        (responseMap, i) -> new ResponseWrapper<>(responseMap,
                                new Pagination(i, filter.getOffset(), filter.getLimit())));
    }

    @Override
    public Uni<List<User>> getUsersByGroup(Group user) {
        return null;
    }
}
