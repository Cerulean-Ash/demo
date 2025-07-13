package com.example.demo.dto.errors;

import java.util.List;

public record BadRequestErrorResponseDto(
        String message,
        List<ErrorDetailDto> details
) {}
