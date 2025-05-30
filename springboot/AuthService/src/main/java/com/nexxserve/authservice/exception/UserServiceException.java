package com.nexxserve.authservice.exception;


import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private final int statusCode;

    public UserServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}