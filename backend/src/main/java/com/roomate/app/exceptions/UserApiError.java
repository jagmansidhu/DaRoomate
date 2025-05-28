package com.roomate.app.exceptions;

public class UserApiError extends Exception{
    public UserApiError(String message) {
        super(message);
    }

    public UserApiError() {
        super("Standard User Api Error");
    }
}
