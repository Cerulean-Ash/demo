package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL;

    @JsonCreator
    public static TransactionType fromString(String text) {
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TransactionType: " + text + ". Must be 'deposit' or 'withdrawal'.");
    }
}