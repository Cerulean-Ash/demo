package com.example.demo.dto.transactions;

import com.example.demo.enums.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateTransactionRequestDto(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        @NotBlank(message = "Currency cannot be empty")
        String currency,

        @NotNull(message = "Transaction type cannot be null")
        TransactionType type,

        @Size(max = 255, message = "Reference cannot exceed 255 characters")
        @Nullable
        String reference
) {}
