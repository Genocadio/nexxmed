package com.nexxserve.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAlreadyExistsException extends UserServiceException {
    // Fix the constructor to match how it's called in the service
    public UserAlreadyExistsException(String message, int statusCode) {
        super(message, statusCode);
    }

    // Add a simpler constructor for convenience
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}