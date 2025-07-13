package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT acc FROM Account acc WHERE acc.accountNumber = ?1 AND acc.isDeleted = false")
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT acc FROM Account acc WHERE acc.user.id = ?1 AND acc.isDeleted = false")
    List<Account> findByUserId(Long userId);
}
