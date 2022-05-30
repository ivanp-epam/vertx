package com.programm.vertx.response;

import java.util.List;
import java.util.Map;

public class ErrorResponse {
    public final int code;
    public final String message;
    public final Map<String, List<ErrorDetail>> details;

    ErrorResponse(int code, String message, Map<String, List<ErrorDetail>> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static ErrorResponse MALFORMED_JSON() {
        return new ErrorResponse(1, "Malformed JSON", Map.of());
    }

    public static ErrorResponse VALIDATION_ERROR(Map<String, List<ErrorDetail>> details) {
        return new ErrorResponse(2, "Validation error", details);
    }

    public static ErrorResponse NOT_FOUND() {
        return new ErrorResponse(3, "Resource not fount", Map.of());
    }
}
