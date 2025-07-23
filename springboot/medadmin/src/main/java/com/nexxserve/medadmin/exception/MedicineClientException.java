package com.nexxserve.medadmin.exception;

public class MedicineClientException extends RuntimeException {

    public MedicineClientException(String message) {
        super(message);
    }

    public MedicineClientException(String message, Throwable cause) {
        super(message, cause);
    }
}