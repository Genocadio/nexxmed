package com.nexxserve.medadmin.exception;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException() {
    }
    public DuplicateEntryException(String message) {
        super(message);
    }
}
