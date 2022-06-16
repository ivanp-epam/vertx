package com.programm.vertx.exceptions;

import am.ik.yavi.core.ConstraintViolation;
import com.programm.vertx.response.error.ErrorCodes;
import com.programm.vertx.response.error.ErrorDetail;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationException extends HttpException {

    private final List<ConstraintViolation> violations;

    public ValidationException(List<ConstraintViolation> violations) {
        this.violations = violations;
    }

    @Override
    public int getHttpCode() {
        return HttpResponseStatus.BAD_REQUEST.code();
    }

    private Map<String, List<ErrorDetail>> details() {
        Map<String, List<ErrorDetail>> details = new HashMap<>();

        for (ConstraintViolation violation : violations) {
            String key = violation.name();

            if (!details.containsKey(key)) {
                details.put(key, new ArrayList<>());
            }

            ErrorDetail errorDetail = new ErrorDetail(violation.messageKey(), violation.message());
            details.get(key).add(errorDetail);
        }

        return details;
    }

    @Override
    public ErrorResponse response() {
        return ErrorResponse.create(ErrorCodes.VALIDATION_ERROR, details());
    }
}
