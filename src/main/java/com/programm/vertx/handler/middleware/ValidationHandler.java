package com.programm.vertx.handler.middleware;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.programm.vertx.exceptions.ValidationException;
import com.programm.vertx.helper.JsonHelper;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.function.Consumer;

public class ValidationHandler<T> implements Consumer<RoutingContext> {

    private final Validator<T> validator;
    private final Class<T> clazz;

    public ValidationHandler(Validator<T> validator, Class<T> clazz) {
        this.validator = validator;
        this.clazz = clazz;
    }

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

    @Override
    public void accept(RoutingContext routingContext) {
        handle(routingContext);
    }
}
