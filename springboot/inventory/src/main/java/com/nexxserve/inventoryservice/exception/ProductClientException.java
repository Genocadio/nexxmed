package com.nexxserve.inventoryservice.exception;

public class ProductClientException extends RuntimeException {

    public ProductClientException(String message) {
        super(message);
    }

    public ProductClientException(String message, Throwable cause) {
        super(message, cause);
    }
}