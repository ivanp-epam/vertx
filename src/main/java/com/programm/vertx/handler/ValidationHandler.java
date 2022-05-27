package com.programm.vertx.handler;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.programm.vertx.http.ResponseHelper;
import com.programm.vertx.http.StatusCodes;
import com.programm.vertx.response.ErrorDetail;
import com.programm.vertx.response.ErrorResponse;
import com.programm.vertx.response.ErrorResponseWrapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationHandler<T> implements Handler<RoutingContext> {

  private final Validator<T> validator;
  private final Class<T> clazz;

  public ValidationHandler(Validator<T> validator, Class<T> clazz) {
    this.validator = validator;
    this.clazz = clazz;
  }

  @Override
  public void handle(RoutingContext event) {
    JsonObject entries = event.body().asJsonObject();
    T object;

    if (entries == null) {

      try {
        object = clazz.getDeclaredConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }

    } else {
      object = entries.mapTo(clazz);
    }

    ConstraintViolations validate = validator.validate(object);

    if (validate.isValid()) {
      event.next();
      return;
    }

    Map<String, List<ErrorDetail>> details = new HashMap<>();

    validate.violations()
      .forEach(constraintViolation -> {
        if (!details.containsKey(constraintViolation.name())) {
          details.put(constraintViolation.name(), new ArrayList<>());
        }

        details.get(constraintViolation.name())
          .add(new ErrorDetail(constraintViolation.messageKey(), constraintViolation.message()));
      });

    ResponseHelper.json(
      event.response(),
      StatusCodes.BAD_REQUEST,
      new ErrorResponseWrapper(ErrorResponse.VALIDATION_ERROR(details))
    );
  }
}
