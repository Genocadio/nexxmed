package com.nexxserve.authservice.exception;

import lombok.Getter;

@Getter
public class BadArgumentException extends RuntimeException{
    final int statusCode;
    public BadArgumentException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
