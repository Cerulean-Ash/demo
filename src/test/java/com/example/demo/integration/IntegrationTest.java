package com.example.demo.integration;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.accounts.AccountResponseDto;
import com.example.demo.dto.accounts.CreateAccountRequestDto;
import com.example.demo.dto.accounts.ListAccountsResponseDto;
import com.example.demo.dto.transactions.CreateTransactionRequestDto;
import com.example.demo.dto.transactions.ListTransactionsResponseDto;
import com.example.demo.dto.users.CreateUserRequestDto;
import com.example.demo.enums.AccountType;
import com.example.demo.enums.TransactionType;
import com.example.demo.model.Address;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private final String userEmail = "testuser@example.com";
    private final String userPassword = "password123";
    private final String accountName = "My Checking Account";
    private String accountNumber;


    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        jwtToken = null;
        accountNumber = null;
    }

    @Test
    @DisplayName("Test full happy path flow: Register, Login, Create Account, Deposit, Withdraw, Verify, Delete Account, Delete User")
    void testFullHappyPathFlow() throws Exception {
        // 1 - User Registration
        Address userAddress = new Address("123 Test St", "Testville", null, "Test City", "Test County", "TS1 2ST");
        CreateUserRequestDto registerRequest = new CreateUserRequestDto(
                userEmail, userPassword, "Test User", userAddress, "+441234567890");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userEmail))
                .andExpect(jsonPath("$.name").value("Test User"));

        logger.info("User registered successfully.");

        // 2 - User Login
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, userPassword);
        MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists())
                .andReturn();

        AuthResponseDto authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponseDto.class);
        jwtToken = authResponse.jwt();
        assertNotNull(jwtToken, "JWT token should not be null after login");
        logger.info("User logged in successfully. JWT: {}", jwtToken);

        // 3 - Create Account
        CreateAccountRequestDto createAccountRequest = new CreateAccountRequestDto(accountName, AccountType.PERSONAL);

        MvcResult createAccountResult = mockMvc.perform(post("/v1/accounts")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(accountName))
                .andExpect(jsonPath("$.accountType").value("PERSONAL"))
                .andExpect(jsonPath("$.balance").value(0.00))
                .andExpect(jsonPath("$.accountNumber").exists())
                .andReturn();

        AccountResponseDto createdAccount = objectMapper.readValue(createAccountResult.getResponse().getContentAsString(), AccountResponseDto.class);
        accountNumber = createdAccount.accountNumber();
        assertNotNull(accountNumber, "Account number should be generated");
        logger.info("Account created successfully: {}", accountNumber);

        // 4 - Deposit Transaction
        BigDecimal depositAmount = new BigDecimal("100.50");
        CreateTransactionRequestDto depositRequest = new CreateTransactionRequestDto(
                depositAmount, "GBP", TransactionType.DEPOSIT, "Initial deposit");

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(depositAmount.doubleValue()))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.reference").value("Initial deposit"));

        logger.info("Deposit transaction successful.");

        // 5 - Withdrawal Transaction
        BigDecimal withdrawalAmount = new BigDecimal("25.25");
        CreateTransactionRequestDto withdrawalRequest = new CreateTransactionRequestDto(
                withdrawalAmount, "GBP", TransactionType.WITHDRAWAL, "Online purchase");

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(withdrawalAmount.doubleValue()))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.reference").value("Online purchase"));

        logger.info("Withdrawal transaction successful.");

        // 6 - Verify Balance
        BigDecimal expectedBalance = depositAmount.subtract(withdrawalAmount);
        MvcResult getAccountResult = mockMvc.perform(get("/v1/accounts/{accountNumber}", accountNumber)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.balance").value(expectedBalance.doubleValue()))
                .andReturn();

        AccountResponseDto updatedAccount = objectMapper.readValue(getAccountResult.getResponse().getContentAsString(), AccountResponseDto.class);
        assertEquals(0, expectedBalance.compareTo(updatedAccount.balance()), "Account balance should be correct");
        logger.info("Account balance verified: {}", updatedAccount.balance());

        // 7 - Retrieve Transactions
        MvcResult getTransactionsResult = mockMvc.perform(get("/v1/accounts/{accountNumber}/transactions", accountNumber)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2))
                .andReturn();

        ListTransactionsResponseDto transactionsResponse = objectMapper.readValue(getTransactionsResult.getResponse().getContentAsString(), ListTransactionsResponseDto.class);
        assertEquals(2, transactionsResponse.transactions().size(), "Should have two transactions");
        logger.info("Transactions retrieved successfully.");

        // 8 - Delete Account
        // Withdraw remaining balance to make it zero for deletion
        BigDecimal remainingBalance = updatedAccount.balance();
        if (remainingBalance.compareTo(BigDecimal.ZERO) > 0) {
            CreateTransactionRequestDto finalWithdrawalRequest = new CreateTransactionRequestDto(
                    remainingBalance, "GBP", TransactionType.WITHDRAWAL, "Zeroing out for deletion");

            mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(finalWithdrawalRequest)))
                    .andExpect(status().isCreated());

            logger.info("Remaining balance withdrawn to zero out account.");

            // Confirm that balance is now zero
            mockMvc.perform(get("/v1/accounts/{accountNumber}", accountNumber)
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(0.00));
            logger.info("Account balance verified as zero.");
        }

        mockMvc.perform(delete("/v1/accounts/{accountNumber}", accountNumber)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent()); // 204 No Content

        logger.info("Account deleted successfully.");

        // Verify account is truly deleted by checking the list of accounts
        MvcResult listAccountsResult = mockMvc.perform(get("/v1/accounts")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").isArray())
                .andReturn();

        ListAccountsResponseDto listedAccounts = objectMapper.readValue(listAccountsResult.getResponse().getContentAsString(), ListAccountsResponseDto.class);
        // Assert that the deleted account's number is NOT present in the list of accounts
        boolean accountFoundAfterDeletion = listedAccounts.accounts().stream()
                .anyMatch(account -> account.accountNumber().equals(accountNumber));
        assertFalse(accountFoundAfterDeletion, "Deleted account should not be found in the list of accounts.");
        logger.info("Account verified as not found in the list after deletion.");


        // 9 - Delete User
        // Get the user ID from the database to delete
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AssertionError("Test user not found after flow"))
                .getId();

        mockMvc.perform(delete("/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        logger.info("User deleted successfully.");

        // Confirm that user is deleted (should return 404)
        mockMvc.perform(get("/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
        logger.info("User verified as not found after deletion.");
    }
}
