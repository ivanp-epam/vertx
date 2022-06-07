package com.programm.vertx.response;

public class ErrorResponseWrapper {
    private final ErrorResponse error;

    public ErrorResponseWrapper(ErrorResponse error) {
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }
}
