package com.example.demo.service;

import com.example.demo.dto.transactions.CreateTransactionRequestDto;
import com.example.demo.dto.transactions.ListTransactionsResponseDto;
import com.example.demo.dto.transactions.TransactionResponseDto;
import com.example.demo.enums.TransactionType;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.AccountOwnershipException;
import com.example.demo.exception.InsufficientFundsException;
import com.example.demo.exception.TransactionNotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, SecurityService securityService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.securityService = securityService;
    }

    @Transactional
    public TransactionResponseDto createTransaction(String accountNumber, CreateTransactionRequestDto request) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        User currentUser = securityService.getCurrentAuthenticatedUser();
        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new AccountOwnershipException("The user is not allowed to access the bank account details.");
        }

        if (!account.getCurrency().equalsIgnoreCase(request.currency())) {
            throw new IllegalArgumentException("Transaction currency must match account currency (" + account.getCurrency() + ").");
        }

        BigDecimal newBalance;
        if (request.type() == TransactionType.DEPOSIT) {
            newBalance = account.getBalance().add(request.amount());
        } else if (request.type() == TransactionType.WITHDRAWAL) {
            if (account.getBalance().compareTo(request.amount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds to process transaction. Current balance: " + account.getBalance());
            }
            newBalance = account.getBalance().subtract(request.amount());
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + request.type());
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction newTransaction = new Transaction(
                account,
                request.amount(),
                request.type(),
                Optional.ofNullable(request.reference()).orElse("")
        );
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        logger.info("Transaction (Type: {}, Amount: {}) created for account {} by user {}. Transaction ID: {}",
                savedTransaction.getTransactionType(), savedTransaction.getAmount(), accountNumber, currentUser.getEmail(), savedTransaction.getId());
        return TransactionResponseDto.fromEntity(savedTransaction, currentUser.getId());
    }

    @Transactional(readOnly = true)
    public ListTransactionsResponseDto getTransactionsByAccountNumber(String accountNumber) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        User currentUser = securityService.getCurrentAuthenticatedUser();
        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new AccountOwnershipException("The user is not allowed to access the transactions.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountId(account.getId());
        List<TransactionResponseDto> transactionResponses = transactions.stream()
                .map(t -> TransactionResponseDto.fromEntity(t, currentUser.getId()))
                .collect(Collectors.toList());

        logger.info("Found {} transactions for account {}.", transactionResponses.size(), accountNumber);
        return new ListTransactionsResponseDto(transactionResponses);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDto getTransactionById(String accountNumber, String transactionId) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }

        Long numericTransactionId;
        try {
            numericTransactionId = Long.parseLong(transactionId);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid transaction ID format. Must be a valid number.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank account was not found."));

        User currentUser = securityService.getCurrentAuthenticatedUser();
        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new AccountOwnershipException("The user is not allowed to access the transaction.");
        }

        Transaction transaction = transactionRepository.findByIdAndAccountId(numericTransactionId, account.getId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId + " for account: " + accountNumber));

        return TransactionResponseDto.fromEntity(transaction, currentUser.getId());
    }
}

