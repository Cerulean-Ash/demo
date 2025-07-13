package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserHasAccountException extends RuntimeException {
    public UserHasAccountException(String message) {
        super(message);
    }
}
