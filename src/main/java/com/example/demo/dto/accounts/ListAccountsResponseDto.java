package com.example.demo.dto.accounts;

import java.util.List;

public record ListAccountsResponseDto(
        List<AccountResponseDto> accounts
) {}
