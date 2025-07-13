package com.example.demo.controller;

import com.example.demo.dto.accounts.AccountResponseDto;
import com.example.demo.dto.accounts.CreateAccountRequestDto;
import com.example.demo.enums.AccountType;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private User testUser;
    private Account testAccount;
    private AccountResponseDto testAccountResponseDto;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("hashedpassword");
        testUser.setRoles("USER");
        testUser.setName("Test User");
        testUser.setCreatedTimestamp(LocalDateTime.now());
        testUser.setUpdatedTimestamp(LocalDateTime.now());

        testAccount = new Account();
        testAccount.setId(100L);
        testAccount.setAccountNumber("12345678");
        testAccount.setName("My Checking");
        testAccount.setAccountType(AccountType.PERSONAL);
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setCurrency("GBP");
        testAccount.setCreatedTimestamp(LocalDateTime.now());
        testAccount.setUpdatedTimestamp(LocalDateTime.now());
        testAccount.setUser(testUser);

        testAccountResponseDto = AccountResponseDto.fromEntity(testAccount);
    }

    @Test
    @DisplayName("Controller: createAccount - Should create an account successfully")
    void controller_createAccount_success() {
        CreateAccountRequestDto request = new CreateAccountRequestDto("New Account", AccountType.PERSONAL);

        AccountResponseDto expectedResponse = new AccountResponseDto(
                "87654321", "10-10-10", "New Account", AccountType.PERSONAL,
                BigDecimal.ZERO, "GBP", LocalDateTime.now(), LocalDateTime.now());

        Mockito.doReturn(expectedResponse).when(accountService).createAccount(request);

        ResponseEntity<AccountResponseDto> responseEntity = accountController.createAccount(request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(accountService, times(1)).createAccount(any(CreateAccountRequestDto.class));
    }

    @Test
    @DisplayName("Controller: getAccountByAccountNumber - Should retrieve an account successfully")
    void controller_getAccountByAccountNumber_success() {
        String accountNumber = "12345678";

        when(accountService.findAccountByAccountNumber(accountNumber)).thenReturn(testAccountResponseDto);

        ResponseEntity<AccountResponseDto> responseEntity = accountController.fetchAccountByAccountNumber(accountNumber);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(testAccountResponseDto, responseEntity.getBody());
        verify(accountService, times(1)).findAccountByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Controller: getAccountByAccountNumber - Should return 404 for not found account")
    void controller_getAccountByAccountNumber_notFound() {
        String accountNumber = "nonexistent";

        when(accountService.findAccountByAccountNumber(accountNumber))
                .thenThrow(new AccountNotFoundException("Bank account was not found."));

        assertThrows(AccountNotFoundException.class, () -> {
            accountController.fetchAccountByAccountNumber(accountNumber);
        });
        verify(accountService, times(1)).findAccountByAccountNumber(accountNumber);
    }
}
