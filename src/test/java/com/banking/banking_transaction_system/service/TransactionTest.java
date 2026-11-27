package com.banking.banking_transaction_system.service;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Transaction;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;
    private Account account;

    @BeforeEach
    void set(){
        account=Account.builder().accountId("A123").balance(new BigDecimal("10000.00")).status("ACTIVE").accountType("SAVINGS").build();
    }
    @Test
    void testDeposit(){
        when(transactionRepository.existsByTransactionId("B123")).thenReturn(false);
        when(accountRepository.findByAccountIdForUpdate("A123")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation->invocation.getArgument(0));
        Transaction result= transactionService.deposit("B123","A123",new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("11000.00"),account.getBalance());
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("DEPOSIT", result.getTransactionType());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));


    }
    @Test
    void testWithdraw(){
        when(transactionRepository.existsByTransactionId("B123")).thenReturn(false);
        when(accountRepository.findByAccountIdForUpdate("A123")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation->invocation.getArgument(0));
        Transaction result= transactionService.withdraw("B123","A123",new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("8000.00"),account.getBalance());
        assertEquals("SUCCESS",result.getStatus());
        assertEquals("WITHDRAWAL",result.getTransactionType());
        verify(accountRepository).save(account);

    }

    @Test
    void testWithdrawWithInsufficientBalance(){
        when(transactionRepository.existsByTransactionId("B123")).thenReturn(false);
        when(accountRepository.findByAccountIdForUpdate("A123")).thenReturn(Optional.of(account));
        assertThrows(RuntimeException.class,()->transactionService.withdraw("B123","A123",new BigDecimal("15000.00")));
        assertEquals(new BigDecimal("10000.00"),account.getBalance());
        verify(accountRepository,never()).save(any(Account.class));
        verify(transactionRepository,never()).save(any(Transaction.class));
    }
    @Test
    void testDuplicateTransactionId(){
        when(transactionRepository.existsByTransactionId("B123")).thenReturn(true);
        assertThrows(RuntimeException.class,()->transactionService.deposit("B123","A123",new BigDecimal("1000.00")));
        assertEquals(new BigDecimal("10000.00"),account.getBalance());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository,never()).save(any(Transaction.class));
    }

    @Test
    void testTransfer(){
        Account destinationAccount= Account.builder().accountId("A223").balance(new BigDecimal("5000.00")).status("ACTIVE").accountType("SAVINGS").build();
        when(transactionRepository.existsByTransactionId("B223")).thenReturn(false);


        when(accountRepository.findByAccountIdForUpdate("A123")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountIdForUpdate("A223")).thenReturn(Optional.of(destinationAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation->invocation.getArgument(0));
        Transaction result=transactionService.transfer("B223","A123","A223", new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("7000.00"),account.getBalance());
        assertEquals(new BigDecimal("8000.00"),destinationAccount.getBalance());
        assertEquals("SUCCESS",result.getStatus());
        assertEquals("TRANSFER",result.getTransactionType());
        verify(accountRepository,times(2)).save(any(Account.class));

    }
}
