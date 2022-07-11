package com.programm.vertx.response.error;

public enum ErrorCodes {
    MALFORMED_JSON(1, "Malformed JSON"),
    VALIDATION_ERROR(2, "Validation error"),
    NOT_FOUND(3, "Resource not found"),
    INTERNAL_SERVER_ERROR(3, "Internal Serer Error"),
    BAD_REQUEST(4, "Bad Request");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
