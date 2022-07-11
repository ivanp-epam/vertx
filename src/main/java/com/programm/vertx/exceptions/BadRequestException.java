package com.programm.vertx.exceptions;

import com.programm.vertx.response.error.ErrorCodes;
import com.programm.vertx.response.error.ErrorResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class BadRequestException extends HttpException {

    @Override
    public int getHttpCode() {
        return HttpResponseStatus.BAD_REQUEST.code();
    }

    @Override
    public ErrorResponse response() {
        return ErrorResponse.create(ErrorCodes.BAD_REQUEST);
    }
}
