package com.programm.vertx.response;

public class Pagination {
    private final long total;
    private final long skip;
    private final long perPage;

    public Pagination(long total, long skip, long perPage) {
        this.total = total;
        this.skip = skip;
        this.perPage = perPage;
    }

    public long getTotal() {
        return total;
    }

    public long getSkip() {
        return skip;
    }

    public long getPerPage() {
        return perPage;
    }
}
