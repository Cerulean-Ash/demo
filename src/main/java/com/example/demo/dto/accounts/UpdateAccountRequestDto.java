package com.example.demo.dto.accounts;

import com.example.demo.enums.AccountType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequestDto(
        @Nullable
        @Size(max = 255, message = "Account name cannot exceed 255 characters")
        String name,
        @Nullable
        AccountType accountType
) {}
