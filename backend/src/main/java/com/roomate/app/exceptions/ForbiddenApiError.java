package com.roomate.app.exceptions;

/**
 * Domain exception for authorization failures (HTTP 403).
 */
public class ForbiddenApiError extends RuntimeException {
    public ForbiddenApiError(String message) {
        super(message);
    }
}
