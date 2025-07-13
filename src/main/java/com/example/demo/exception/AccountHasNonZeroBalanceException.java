package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AccountHasNonZeroBalanceException extends RuntimeException {
    public AccountHasNonZeroBalanceException(String message) {
        super(message);
    }
}
