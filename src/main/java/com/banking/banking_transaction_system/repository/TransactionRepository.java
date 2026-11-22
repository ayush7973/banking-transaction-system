package com.banking.banking_transaction_system.repository;

import com.banking.banking_transaction_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByTransactionType(String transactionType);
    List<Transaction> findByDestAccountId(String destAccountId);
    List<Transaction> findBySourceAccountId(String sourceAccountId);
}
