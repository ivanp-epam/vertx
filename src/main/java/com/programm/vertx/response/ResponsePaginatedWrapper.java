package com.programm.vertx.response;

public class ResponsePaginatedWrapper<T> {
    private final T data;
    private final Pagination pagination;

    public ResponsePaginatedWrapper(T data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public T getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
