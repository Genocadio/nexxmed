package com.nexxserve.medadmin.exception;

import java.util.UUID;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(UUID categoryId) {
        super("Category with ID " + categoryId + " cannot be deleted as it is referenced by product families");
    }
}