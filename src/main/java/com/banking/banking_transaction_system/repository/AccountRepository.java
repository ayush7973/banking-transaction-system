package com.banking.banking_transaction_system.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.banking.banking_transaction_system.entity.Account;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
    List<Account> findByCustomerCustomerId(String customerId);
    Optional<Account> findByAccountId(String accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId= :accountId")
    Optional<Account>findByAccountIdForUpdate(@Param("accountId") String accountId);
    List<Account> findByStatus(String status);


}
