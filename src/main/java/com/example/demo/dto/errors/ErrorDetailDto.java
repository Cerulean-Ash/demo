package com.example.demo.dto.errors;

public record ErrorDetailDto(
        String field,
        String message,
        String type
) {}
