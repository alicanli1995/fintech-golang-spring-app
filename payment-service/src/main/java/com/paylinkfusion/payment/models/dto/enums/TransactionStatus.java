package com.paylinkfusion.payment.models.dto.enums;

public enum TransactionStatus {

    PENDING_PROCESSING("PENDING_PROCESSING"),
    PENDING_QUEUE("PENDING_QUEUE"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
