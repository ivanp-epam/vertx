package com.programm.vertx.validators;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.constraint.CharSequenceConstraint;
import am.ik.yavi.core.Validator;
import com.programm.vertx.entities.Permission;
import com.programm.vertx.request.UserIdsRequest;
import com.programm.vertx.request.GroupRequest;
import com.programm.vertx.request.UserRequest;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Validators {
    public static final Validator<UserRequest> USER_VALIDATOR = ValidatorBuilder.<UserRequest>of()
            .constraint(UserRequest::getAge, "age", c -> c
                    .notNull()
                    .greaterThanOrEqual(4)
                    .lessThanOrEqual(130)
            )
            .constraint(UserRequest::getLogin, "login", CharSequenceConstraint::notBlank)
            .constraint(UserRequest::getPassword, "password", c -> c
                    .notBlank()
                    .pattern("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
                    .message("Password must contain at least 8 characters and has one letter and one number")
            )
            .build();


    public static final Validator<GroupRequest> GROUP_VALIDATOR = ValidatorBuilder.<GroupRequest>of()
            .constraint(GroupRequest::getName, "name", CharSequenceConstraint::notBlank)
            .forEach(GroupRequest::getPermissions, "permissions", c -> c.constraint(String::toString, "", c1 ->
                            c1.oneOf(Arrays.stream(Permission.values())
                                            .map(Permission::name)
                                            .collect(Collectors.toList()))
                                    .message("Not Valid Input Values")
                    )
            )
            .build();

    public static final Validator<UserIdsRequest> GROUP_IDS_VALIDATOR = ValidatorBuilder.<UserIdsRequest>of()
            .forEach(UserIdsRequest::getUserIds, "userIds", c -> c.constraint(String::toString, "", c1 -> c1.notNull().uuid()))
            .build();

}
