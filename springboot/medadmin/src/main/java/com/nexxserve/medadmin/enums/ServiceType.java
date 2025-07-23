package com.nexxserve.medadmin.enums;

public enum ServiceType {
    PHARMACY(40.0),
    CLINIC_EMR(100.0);

    private final double price;

    ServiceType(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}