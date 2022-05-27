package com.programm.vertx.validators;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.constraint.CharSequenceConstraint;
import am.ik.yavi.core.Validator;
import com.programm.vertx.dto.UserInput;

public class UsersValidator {
  public static final Validator<UserInput> validator = ValidatorBuilder.<UserInput>of()
    .constraint(UserInput::getAge, "age", c -> c
      .notNull()
      .greaterThanOrEqual(4)
      .lessThanOrEqual(130)
    )
    .constraint(UserInput::getLogin, "login", CharSequenceConstraint::notBlank)
    .constraint(UserInput::getPassword, "password", c -> c
      .notBlank()
      .pattern("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        .message("Password must contain at least 8 characters and has one letter and one number")
    )
    .build();

}
