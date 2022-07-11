package com.programm.vertx.response;

public class ResponseWrapper<T> {
    private final T data;

    public ResponseWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
