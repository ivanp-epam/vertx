package com.programm.vertx.response;

public class ErrorDetail {
    private final String key;
    private final String message;

    public ErrorDetail(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }
}
