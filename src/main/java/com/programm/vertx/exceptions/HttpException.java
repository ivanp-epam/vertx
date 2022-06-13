package com.programm.vertx.exceptions;

import com.programm.vertx.response.error.ErrorResponse;

abstract public class HttpException extends RuntimeException {
    abstract public int getHttpCode();
    abstract public ErrorResponse response();
}
