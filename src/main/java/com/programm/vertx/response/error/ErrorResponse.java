package com.programm.vertx.response.error;

import java.util.List;
import java.util.Map;

public class ErrorResponse {
    private final ErrorObject error;

    public ErrorResponse(ErrorObject error) {
        this.error = error;
    }

    public static ErrorResponse create(ErrorCodes error, Map<String, List<ErrorDetail>> details) {
        ErrorObject errorObject = new ErrorObject(error.getCode(), error.getMessage(), details);
        return new ErrorResponse(errorObject);
    }

    public static ErrorResponse create(ErrorCodes error) {
        return ErrorResponse.create(error, Map.of());
    }

    public ErrorObject getError() {
        return error;
    }
}
