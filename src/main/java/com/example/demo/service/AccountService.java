package com.example.demo.service;

import com.example.demo.dto.accounts.AccountResponseDto;
import com.example.demo.dto.accounts.CreateAccountRequestDto;
import com.example.demo.dto.accounts.ListAccountsResponseDto;
import com.example.demo.dto.accounts.UpdateAccountRequestDto;
import com.example.demo.exception.AccountHasNonZeroBalanceException;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.AccountOwnershipException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, SecurityService securityService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }


    @Transactional
    public AccountResponseDto createAccount(CreateAccountRequestDto request) {

        User currentUser = securityService.getCurrentAuthenticatedUser();

        String newAccountNumber;
        do {
            newAccountNumber = generateUniqueAccountNumber();
        } while (accountRepository.findByAccountNumber(newAccountNumber).isPresent());

        Account newAccount = new Account(
                request.name(),
                request.accountType(),
                currentUser
        );
        newAccount.setAccountNumber(newAccountNumber);
        newAccount.setDeleted(false);

        Account savedAccount = accountRepository.save(newAccount);
        logger.info("Account {} created successfully for user {}.", savedAccount.getAccountNumber(), currentUser.getEmail());
        return AccountResponseDto.fromEntity(savedAccount);
    }


    @Transactional(readOnly = true)
    public AccountResponseDto findAccountByAccountNumber(String accountNumber) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        checkAccountOwnership(account);
        return AccountResponseDto.fromEntity(account);
    }


    @Transactional(readOnly = true)
    public ListAccountsResponseDto findAllAccountsForCurrentUser() {
        User currentUser = securityService.getCurrentAuthenticatedUser();
        List<Account> accounts = accountRepository.findByUserId(currentUser.getId());
        List<AccountResponseDto> accountResponses = accounts.stream()
                .map(AccountResponseDto::fromEntity)
                .collect(Collectors.toList());

        logger.info("Found {} accounts for user {}.", accountResponses.size(), currentUser.getEmail());
        return new ListAccountsResponseDto(accountResponses);
    }


    @Transactional
    public AccountResponseDto updateAccount(String accountNumber, UpdateAccountRequestDto request) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }

        Account existingAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        checkAccountOwnership(existingAccount);
        if (request.name() != null && !request.name().trim().isEmpty()) {
            existingAccount.setName(request.name());
        }
        if (request.accountType() != null) {
            existingAccount.setAccountType(request.accountType());
        }

        Account updatedAccount = accountRepository.save(existingAccount);
        logger.info("Account {} updated successfully.", updatedAccount.getAccountNumber());
        return AccountResponseDto.fromEntity(updatedAccount);
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }

        Account accountToDelete = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        checkAccountOwnership(accountToDelete);

        // Prevent deletion if balance is not zero
        if (accountToDelete.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountHasNonZeroBalanceException("Account cannot be deleted if the balance is not zero.");
        }

        // Perform soft deletion - set the isDeleted flag to true
        accountToDelete.setDeleted(true);
        accountRepository.save(accountToDelete);

        logger.info("Account {} deleted successfully.", accountNumber);
    }


    private String generateUniqueAccountNumber() {
        // Generate a random 8-digit number
        // Using a simple approach
        return String.format("%08d", (int) (Math.random() * 100_000_000));
    }

    private void checkAccountOwnership(Account account) {
        User currentUser = securityService.getCurrentAuthenticatedUser();
        boolean isOwner = account.getUser().getId().equals(currentUser.getId());
        if (!isOwner) {
            throw new AccountOwnershipException("The user is not allowed to access the bank account details.");
        }
    }
}
