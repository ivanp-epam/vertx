package com.programm.vertx.handler;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.programm.vertx.exceptions.ValidationException;
import com.programm.vertx.helper.JsonHelper;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ValidationHandler<T> implements Handler<RoutingContext> {

    private final Validator<T> validator;
    private final Class<T> clazz;

    public ValidationHandler(Validator<T> validator, Class<T> clazz) {
        this.validator = validator;
        this.clazz = clazz;
    }

    @Override
    public void handle(RoutingContext event) {
        String entries = event.body().asString();

        T object = JsonHelper.fromJsonObject(entries, clazz);
        ConstraintViolations validate = validator.validate(object);

        if (validate.isValid()) {
            event.next();
            return;
        }

        throw new ValidationException(validate.violations());
    }
}
