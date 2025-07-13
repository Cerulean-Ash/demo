package com.example.demo.dto.accounts;

import com.example.demo.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequestDto(
        @NotBlank(message = "Account name cannot be empty")
        String name,
        @NotNull(message = "Account type cannot be null")
        AccountType accountType
) {}