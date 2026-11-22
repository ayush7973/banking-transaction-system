package com.banking.banking_transaction_system.repository;

import com.banking.banking_transaction_system.entity.FailedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedTransactionRepository extends JpaRepository<FailedTransaction,Long> {
    List<FailedTransaction> findByTransactionId(String transactionId);
    List<FailedTransaction> findByErrorCategory(String errorCategory);

}
