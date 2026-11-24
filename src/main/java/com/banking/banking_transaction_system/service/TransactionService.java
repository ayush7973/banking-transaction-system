package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Transaction;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
     }
    @Transactional
    public Transaction deposit(String transactionId, String accountId, BigDecimal amount){
        validateAmount(amount);
        Account account = accountRepository.findByAccountIdForUpdate(accountId).orElseThrow(()->new RuntimeException("Account not exist"));
        validateAccount(account);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return saveTransaction(transactionId,"DEPOSIT",amount, null,accountId);
    }
    private void validateAmount(BigDecimal amount){
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new RuntimeException("Amount not valid");
        }

    }
      private void validateAccount(Account account){
        if(!"Active".equals(account.getStatus())){
            throw new RuntimeException("Account is inactive");
        }
          
    }
    @Transactional
    public Transaction withdraw(String transactionId,String accountId, BigDecimal amount){
        validateAmount(amount);
        Account account=accountRepository.findByAccountIdForUpdate(accountId).orElseThrow(()->new RuntimeException("Account not exist"));
        validateAccount(account);
        if(account.getBalance().compareTo(amount)<0){
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return saveTransaction(transactionId, "WITHDRAWAL", amount, accountId,null);
    }
    public Transaction transfer(String transactionId, String sourceAccountId, String destAccountId, BigDecimal amount){
        validateAmount(amount);
        if(sourceAccountId.equals(destAccountId)){
            throw new RuntimeException("Source and destination is same");
        }
//        if(firstAccountId.compareTo(destAccountId))
        String firstAccountId=sourceAccountId.compareTo(destAccountId)<0?sourceAccountId:destAccountId;
        String secondAccountId=sourceAccountId.compareTo(destAccountId)<0?destAccountId:sourceAccountId;
        Account firstAccount=accountRepository.findByAccountIdForUpdate(firstAccountId).orElseThrow(()->new RuntimeException("Account not exist"));
        Account secondAccount=accountRepository.findByAccountIdForUpdate(secondAccountId).orElseThrow(()->new RuntimeException("Account not exist"));
        Account sourceAccount=sourceAccountId.equals(firstAccountId)?firstAccount:secondAccount;
        Account destAccount=destAccountId.equals(firstAccountId)?firstAccount:secondAccount;
        validateAccount(sourceAccount);
        validateAccount(destAccount);
        if(sourceAccount.getBalance().compareTo(amount)<0){
            throw new RuntimeException("Insufficient balance");
        }
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destAccount.setBalance(destAccount.getBalance().add(amount));
        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);
        return saveTransaction(transactionId,"TRANSFER",amount,sourceAccountId,destAccountId);


    }
     private Transaction saveTransaction(String transactionId, String type, BigDecimal amount, String sourceAccountId, String destAccountId){
        Transaction transaction=Transaction.builder().transactionId(transactionId).transactionType(type).amount(amount)
                .sourceAccountId(sourceAccountId).destAccountId(destAccountId).status("SUCCESS").createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now()).build();
        return transactionRepository.save(transaction);
     }


}
