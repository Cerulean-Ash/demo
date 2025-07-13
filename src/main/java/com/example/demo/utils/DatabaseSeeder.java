package com.example.demo.utils;

import com.example.demo.enums.AccountType;
import com.example.demo.model.Account;
import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository,
                          AccountRepository accountRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Seeding database with initial user and account data...");

        // 1. Create Users
        Optional<User> existingAdmin = userRepository.findByEmail("admin@bank.com");
        User adminUser;
        if (existingAdmin.isEmpty()) {
            Address adminAddress = new Address("10 Admin Street", "Adminville", null, "Admin Town", "Admin County", "AD1 1AD");

            adminUser = new User("admin@email.com", passwordEncoder.encode("adminpass"), "ADMIN,USER",
                    "Admin User", adminAddress, "+447700123456");
            userRepository.save(adminUser);
            logger.info("Created admin user: {}", adminUser.getEmail());
        } else {
            adminUser = existingAdmin.get();
            logger.info("Admin user already exists: {}", adminUser.getEmail());
        }

        Optional<User> existingTestUser = userRepository.findByEmail("testuser@email.com");
        User testUser;
        if (existingTestUser.isEmpty()) {
            Address testUserAddress = new Address("20 Test Road", "Testing Heights", "Apt 1", "Test City", "Test County", "TS1 1TS");
            // The first argument to the User constructor is now the email address
            testUser = new User("testuser@eaglebank.com", passwordEncoder.encode("password123"), "USER",
                    "Test User", testUserAddress, "+447800987654");
            userRepository.save(testUser);
            logger.info("Created testuser: {}", testUser.getEmail());
        } else {
            testUser = existingTestUser.get();
            logger.info("Test user already exists: {}", testUser.getEmail());
        }

        // 2. Create Accounts for the seeded users
        if (accountRepository.findByUserId(adminUser.getId()).isEmpty()) {
            Account adminSavings = new Account("Admin Savings Account", AccountType.PERSONAL, adminUser);
            adminSavings.setAccountNumber("01" + String.format("%06d", (int) (Math.random() * 1_000_000)));
            adminSavings.setBalance(new BigDecimal("10000.00"));
            accountRepository.save(adminSavings);
            logger.info("Created savings account {} for admin.", adminSavings.getAccountNumber());

            Account adminBusiness = new Account("Admin Business Account", AccountType.BUSINESS, adminUser);
            adminBusiness.setAccountNumber("01" + String.format("%06d", (int) (Math.random() * 1_000_000)));
            adminBusiness.setBalance(new BigDecimal("50000.00"));
            accountRepository.save(adminBusiness);
            logger.info("Created business account {} for admin.", adminBusiness.getAccountNumber());
        } else {
            logger.info("Admin user already has accounts.");
        }

        logger.info("Database seeding complete.");
    };
}

