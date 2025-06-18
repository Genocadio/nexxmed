package com.nexxserve.inventoryservice.exception;

public class MedicineValidationException extends RuntimeException {
    public MedicineValidationException(String message) {
        super(message);
    }

    public MedicineValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}