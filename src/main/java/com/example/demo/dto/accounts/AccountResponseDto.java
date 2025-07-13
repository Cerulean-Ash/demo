package com.example.demo.dto.accounts;

import com.example.demo.enums.AccountType;
import com.example.demo.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponseDto(
        String accountNumber,
        String sortCode,
        String name,
        AccountType accountType,
        BigDecimal balance,
        String currency,
        LocalDateTime createdTimestamp,
        LocalDateTime updatedTimestamp
) {

    public static AccountResponseDto fromEntity(Account account) {
        return new AccountResponseDto(
                account.getAccountNumber(),
                account.getSortCode(),
                account.getName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedTimestamp(),
                account.getUpdatedTimestamp()
        );
    }
}
