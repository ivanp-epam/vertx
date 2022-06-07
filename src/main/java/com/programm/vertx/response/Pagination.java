package com.programm.vertx.response;

public class Pagination {
    private final int total;
    private final int skip;
    private final int perPage;

    public Pagination(int total, int skip, int perPage) {
        this.total = total;
        this.skip = skip;
        this.perPage = perPage;
    }

    public int getTotal() {
        return total;
    }

    public int getSkip() {
        return skip;
    }

    public int getPerPage() {
        return perPage;
    }
}
