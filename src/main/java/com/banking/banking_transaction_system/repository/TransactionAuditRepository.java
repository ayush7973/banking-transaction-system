package com.banking.banking_transaction_system.repository;

import com.banking.banking_transaction_system.entity.TransactionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionAuditRepository extends JpaRepository<TransactionAudit,Long> {
    List<TransactionAudit> findByTransactionId(String transactionId);
}
