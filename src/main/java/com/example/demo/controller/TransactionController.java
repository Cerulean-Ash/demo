package com.example.demo.controller;

import com.example.demo.dto.errors.BadRequestErrorResponseDto;
import com.example.demo.dto.errors.ErrorResponseDto;
import com.example.demo.dto.transactions.CreateTransactionRequestDto;
import com.example.demo.dto.transactions.ListTransactionsResponseDto;
import com.example.demo.dto.transactions.TransactionResponseDto;
import com.example.demo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
@Tag(name = "transaction", description = "Manage transactions on a bank account")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Create a transaction",
            description = "Create a transaction",
            operationId = "createTransaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction has been created successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid details supplied",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to delete the bank account details",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "422", description = "Insufficient funds to process transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateTransactionRequestDto request) {
        logger.info("Received request to create transaction for account {}. Type: {}, Amount: {}",
                accountNumber, request.type(), request.amount());
        TransactionResponseDto createdTransaction = transactionService.createTransaction(accountNumber, request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @Operation(summary = "List transactions",
            description = "List transactions",
            operationId = "listAccountTransaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of transaction details",
                    content = @Content(schema = @Schema(implementation = ListTransactionsResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transactions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ListTransactionsResponseDto> listTransactions(
            @PathVariable String accountNumber) {
        logger.info("Received request to list transactions for account: {}", accountNumber);
        ListTransactionsResponseDto transactionList = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactionList);
    }

    @Operation(summary = "Fetch transaction by ID.",
            description = "Fetch transaction by ID.",
            operationId = "fetchAccountTransactionByID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The transaction details",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> fetchTransactionById(
            @PathVariable String accountNumber,
            @PathVariable String transactionId) {
        logger.info("Received request to fetch transaction {} for account {}.", transactionId, accountNumber);
        TransactionResponseDto transaction = transactionService.getTransactionById(accountNumber, transactionId);
        return ResponseEntity.ok(transaction);
    }
}
