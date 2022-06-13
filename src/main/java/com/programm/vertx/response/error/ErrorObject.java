package com.programm.vertx.response.error;

import java.util.List;
import java.util.Map;

public class ErrorObject {
    public final int code;
    public final String message;
    public final Map<String, List<ErrorDetail>> details;

    ErrorObject(int code, String message, Map<String, List<ErrorDetail>> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public ErrorObject(int code, String message) {
        this(code,message, Map.of());
    }

    public ErrorObject(ErrorCodes error) {
        this(error.getCode(),error.getMessage(), Map.of());
    }

    public ErrorObject(ErrorCodes error, Map<String, List<ErrorDetail>> details) {
        this(error.getCode(),error.getMessage(), details);
    }
}
