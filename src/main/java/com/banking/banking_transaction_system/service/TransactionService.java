
package com.banking.banking_transaction_system.service;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Transaction;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Service
public class TransactionService{
    private static final Logger logger=LoggerFactory.getLogger(TransactionService.class);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
     public TransactionService(AccountRepository accountRepository,TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
     }
    @Transactional
    public Transaction deposit(String transactionId,String accountId,BigDecimal amount){
        long startTime=System.currentTimeMillis();
        logger.info("Deposit initiated |TransactionId:{}| AccountId:{}| Thread:{}",transactionId,accountId,Thread.currentThread().getName());
        try{
            validateTransactionId(transactionId);
            validateAmount(amount);
            Account account = accountRepository.findByAccountIdForUpdate(accountId).orElseThrow(()->new RuntimeException("Account not exist"));
            validateAccount(account);
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);
            Transaction transaction=saveTransaction(transactionId,"DEPOSIT",amount,null,accountId);
            logger.info("Deposit done ,Transaction ID:{}, Account ID:{},Duration:{}ms",transactionId,accountId,System.currentTimeMillis()-startTime);
            return transaction;
        }
         catch(RuntimeException e){
            logger.warn("Deposit failed, Transaction ID:{} ,Account ID:{} ,Error Category:{} ,Reason:{},Duration:{}ms",transactionId,accountId,e.getClass().getSimpleName(),e.getMessage(),System.currentTimeMillis()-startTime);
            throw e;
        }
    }
    @Transactional
    public Transaction withdraw(String transactionId,String accountId,BigDecimal amount){
        long startTime=System.currentTimeMillis();
        logger.info("Withdrawal initiated,TransactionId:{},AccountId:{},Thread:{}",transactionId,accountId,Thread.currentThread().getName());
        try{
            validateTransactionId(transactionId);
            validateAmount(amount);
            Account account = accountRepository.findByAccountIdForUpdate(accountId).orElseThrow(()->new RuntimeException("Account not exist"));
            validateAccount(account);
            if (account.getBalance().compareTo(amount) <0){
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
            Transaction transaction=saveTransaction(transactionId,"WITHDRAWAL",amount,accountId,null);
            logger.info("Withdrawal successful, TransactionId:{},AccountId:{}, Duration:{}ms",transactionId,accountId,System.currentTimeMillis()-startTime);
            return transaction;
        }
        catch(RuntimeException e){
            logger.warn("Withdrawal failed,TransactionId:{} ,AccountId:{},Error Category:{} ,Reason:{} ,Duration:{}ms",transactionId,accountId,e.getClass().getSimpleName(),e.getMessage(),System.currentTimeMillis()-startTime);
            throw e;
        }
    }
    @Transactional
    public Transaction transfer(String transactionId, String sourceAccountId,String destAccountId,BigDecimal amount){
        long startTime = System.currentTimeMillis();
        logger.info("Transfer initiated,Transaction Id:{} ,Source Account:{} ,Destination Account:{} ,Thread:{}",transactionId,sourceAccountId,destAccountId,Thread.currentThread().getName());
        try{
            validateTransactionId(transactionId);
            validateAmount(amount);
            if (sourceAccountId.equals(destAccountId)) {
                throw new RuntimeException("Source and destination is same");
            }
            String firstAccountId =sourceAccountId.compareTo(destAccountId)< 0?sourceAccountId:destAccountId;
            String secondAccountId =sourceAccountId.compareTo(destAccountId)< 0?destAccountId:sourceAccountId;
            Account firstAccount =accountRepository.findByAccountIdForUpdate(firstAccountId).orElseThrow(()->new RuntimeException("Account not exist"));
            Account secondAccount =accountRepository.findByAccountIdForUpdate(secondAccountId).orElseThrow(()->new RuntimeException("Account not exist"));
            Account sourceAccount =sourceAccountId.equals(firstAccountId)? firstAccount:secondAccount;
            Account destAccount =destAccountId.equals(firstAccountId)? firstAccount:secondAccount;
            validateAccount(sourceAccount);
            validateAccount(destAccount);
            if(sourceAccount.getBalance().compareTo(amount)<0){
                throw new RuntimeException("Insufficient balance");
            }
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            destAccount.setBalance(destAccount.getBalance().add(amount));
            accountRepository.save(sourceAccount);
            accountRepository.save(destAccount);
            Transaction transaction =saveTransaction(transactionId,"TRANSFER",amount, sourceAccountId,destAccountId);
            logger.info("Transfer successful,TransactionId:{}, Source Account:{},Destination Account:{}, Duration:{}ms",transactionId,sourceAccountId,destAccountId,System.currentTimeMillis()-startTime);
            return transaction;
        }
        catch(RuntimeException e){
            logger.warn("Transfer failed,TransactionId:{} ,Source Account:{} ,Destination Account:{},Error Category:{},Reason:{},Duration:{}ms",transactionId,sourceAccountId,destAccountId,e.getClass().getSimpleName(),e.getMessage(),System.currentTimeMillis()-startTime);
            throw e;
        }
    }
     private void validateAmount(BigDecimal amount){
        if (amount==null|| amount.compareTo(BigDecimal.ZERO)<= 0){
            throw new RuntimeException("Amount not valid");
        }
    }
    private void validateAccount(Account account){
        if (!"ACTIVE".equals(account.getStatus())){
            throw new RuntimeException("Account is not active");
        }
    }
    private void validateTransactionId(String transactionId){
        if (transactionRepository.existsByTransactionId(transactionId)){
            throw new RuntimeException("Duplicate transaction id");
        }
    }
    private Transaction saveTransaction(String transactionId,String type,BigDecimal amount,String sourceAccountId,String destAccountId){
        Transaction transaction=Transaction.builder().transactionId(transactionId).transactionType(type).amount(amount).sourceAccountId(sourceAccountId).destAccountId(destAccountId).status("SUCCESS").createdAt(LocalDateTime.now()).processedAt(LocalDateTime.now()).build();
        return transactionRepository.save(transaction);
    }
}
