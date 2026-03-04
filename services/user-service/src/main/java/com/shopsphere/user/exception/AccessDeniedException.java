package com.shopsphere.user.exception;

/**
 * AccessDeniedException - Thrown when user attempts to access/modify resources they don't own
 * and don't have permission to access (not an admin)
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}

