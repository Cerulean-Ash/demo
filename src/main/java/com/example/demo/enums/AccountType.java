package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccountType {
    PERSONAL,
    BUSINESS;

    @JsonCreator
    public static AccountType fromString(String text) {
        for (AccountType type : AccountType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AccountType: " + text + ". Must be 'personal' or 'business'.");
    }
}

