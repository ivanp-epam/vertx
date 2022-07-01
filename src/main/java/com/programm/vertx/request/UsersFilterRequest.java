package com.programm.vertx.request;

import com.programm.vertx.helper.IntegerHelper;

public class UsersFilterRequest {

    private final int defaultOffset = 0;
    private final int defaultLimit = 10;


    private final String startFrom;
    private final int limit;
    private final int offset;

    public UsersFilterRequest(String startFrom, String limit, String offset) {
        this.startFrom = startFrom;
        this.limit = IntegerHelper.tryParseInt(limit, defaultLimit);
        this.offset = IntegerHelper.tryParseInt(offset, defaultOffset);
    }

    public String getStartFrom() {
        return startFrom;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
