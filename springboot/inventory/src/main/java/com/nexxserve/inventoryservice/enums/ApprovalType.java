package com.nexxserve.inventoryservice.enums;

public enum ApprovalType {
    AUTOMATIC,          // No pre-approval needed
    MANUAL_APPROVAL,    // Requires manual approval from insurance
    CONDITIONAL,        // Approval based on specific conditions
    PRE_AUTHORIZATION   // Requires pre-authorization with reference number
}