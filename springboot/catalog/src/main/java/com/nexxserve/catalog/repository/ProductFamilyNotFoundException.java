package com.nexxserve.catalog.exception;

import java.util.UUID;

public class ProductFamilyNotFoundException extends RuntimeException {

    public ProductFamilyNotFoundException(UUID id) {
        super("Product family not found with id: " + id);
    }

    public ProductFamilyNotFoundException(String message) {
        super(message);
    }
}