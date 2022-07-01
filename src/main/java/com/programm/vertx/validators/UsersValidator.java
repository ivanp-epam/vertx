package com.programm.vertx.validators;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.constraint.CharSequenceConstraint;
import am.ik.yavi.core.Validator;
import com.programm.vertx.request.UserRequest;

public class UsersValidator {
    public static final Validator<UserRequest> validator = ValidatorBuilder.<UserRequest>of()
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

}
