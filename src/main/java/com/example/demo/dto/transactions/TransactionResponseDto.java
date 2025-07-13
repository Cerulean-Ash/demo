package com.example.demo.dto.transactions;

import com.example.demo.enums.TransactionType;
import com.example.demo.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        Long id,
        BigDecimal amount,
        String currency,
        TransactionType type,
        String reference,
        Long userId,
        LocalDateTime createdTimestamp
) {

    public static TransactionResponseDto fromEntity(Transaction transaction, Long userId) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getDescription(),
                userId,
                transaction.getTimestamp()
        );
    }
}
