package com.example.demo.dto.transactions;

import java.util.List;

public record ListTransactionsResponseDto(
        List<TransactionResponseDto> transactions
) { }
