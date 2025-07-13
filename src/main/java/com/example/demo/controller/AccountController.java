package com.example.demo.controller;

import com.example.demo.dto.accounts.AccountResponseDto;
import com.example.demo.dto.accounts.CreateAccountRequestDto;
import com.example.demo.dto.accounts.ListAccountsResponseDto;
import com.example.demo.dto.accounts.UpdateAccountRequestDto;
import com.example.demo.dto.errors.BadRequestErrorResponseDto;
import com.example.demo.dto.errors.ErrorResponseDto;
import com.example.demo.service.AccountService;
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
@RequestMapping("/v1/accounts")
@Tag(name = "account", description = "Manage a bank account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new bank account",
            description = "Create a new bank account",
            operationId = "createAccount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bank Account has been created successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid details supplied",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody CreateAccountRequestDto request) {
        logger.info("Received request to create account for authenticated user. Account name: {}, type: {}", request.name(), request.accountType());
        AccountResponseDto createdAccount = accountService.createAccount(request);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @Operation(summary = "List accounts",
            description = "List accounts",
            operationId = "listAccounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of bank accounts",
                    content = @Content(schema = @Schema(implementation = ListAccountsResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ListAccountsResponseDto> listAccounts() {
        logger.info("Received request to list accounts for authenticated user.");
        ListAccountsResponseDto accounts = accountService.findAllAccountsForCurrentUser();
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Fetch account by account number.",
            description = "Fetch account by account number.",
            operationId = "fetchAccountByAccountNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The bank account details",
                    content = @Content(schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "The user was not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the bank account details",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> fetchAccountByAccountNumber(@PathVariable String accountNumber) {
        logger.info("Received request to fetch account by number: {}", accountNumber);
        AccountResponseDto account = accountService.findAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Update account by account number.",
            description = "Update account by account number.",
            operationId = "updateAccountByAccountNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The updated bank account details",
                    content = @Content(schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to update the bank account details",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> updateAccountByAccountNumber(@PathVariable String accountNumber, @Valid @RequestBody UpdateAccountRequestDto request) {
        logger.info("Received request to update account with number: {}", accountNumber);
        AccountResponseDto updatedAccount = accountService.updateAccount(accountNumber, request);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Delete account by account number.",
            description = "Delete account by account number.",
            operationId = "deleteAccountByAccountNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The bank account has been deleted"),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to delete the bank account details",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Account has non-zero balance",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccountByAccountNumber(@PathVariable String accountNumber) {
        logger.info("Received request to delete account with number: {}", accountNumber);
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}

