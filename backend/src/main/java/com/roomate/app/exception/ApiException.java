package com.roomate.app.exception;

public class ApiException extends RuntimeException {
    public ApiException(String s) {
        super(s);
    }
    public ApiException() {
        super("An error occurred:");
    }
}
