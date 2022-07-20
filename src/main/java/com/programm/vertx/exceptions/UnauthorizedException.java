package com.programm.vertx.exceptions;

import com.programm.vertx.response.error.ErrorCodes;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class UnauthorizedException extends HttpException {

    @Override
    public int getHttpCode() {
        return HttpResponseStatus.UNAUTHORIZED.code();
    }

    @Override
    public ErrorResponse response() {
        return ErrorResponse.create(ErrorCodes.UNAUTHORIZED);
    }
}
