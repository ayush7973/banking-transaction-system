package com.banking.banking_transaction_system.service;
import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace =AutoConfigureTestDatabase.Replace.NONE)
public class ConcurrencyTest {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Test
    void testConcurrentWithdrawals() throws Exception{
        String uniqueId = UUID.randomUUID().toString().substring(0,8);
        Customer customer = Customer.builder().customerId("CT"+ uniqueId).fullName("Rahul Tiwari").email("rahul" + uniqueId + "@rahul.com").phone("9876501234").build();
        customerRepository.saveAndFlush(customer);
        String accountId = "AT" +uniqueId;
        Account account = Account.builder().accountId(accountId).customer(customer).accountType("SAVINGS").balance(new BigDecimal("10000.00")).status("ACTIVE").build();
        accountRepository.saveAndFlush(account);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try{
            Callable<Boolean> withdrawal1 =()->{
                ready.countDown();
                start.await();
                try
                {
                    transactionService.withdraw("T1" + uniqueId, accountId, new BigDecimal("7000.00"));
                    return true;

                }
                catch(RuntimeException e){
                    if ("Insufficient balance".equals(e.getMessage())) {
                        return false;
                    }
                    throw e;
                }
            };
            Callable<Boolean> withdrawal2 =()->{ready.countDown();start.await();
                try
                {
                    transactionService.withdraw("T2" +uniqueId, accountId,new BigDecimal("5000.00"));
                    return true;
                }
                catch(RuntimeException e){
                    if ("Insufficient balance".equals(e.getMessage())){
                        return false;
                    }
                    throw e;
                }
            };
            Future<Boolean> result1 =executor.submit(withdrawal1);
            Future<Boolean> result2 =executor.submit(withdrawal2);
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            boolean withdrawal1D =result1.get(20,TimeUnit.SECONDS);
            boolean withdrawal2D =result2.get(20,TimeUnit.SECONDS);
            int successCount = 0;
            if (withdrawal1D){
                successCount++;
            }
            if (withdrawal2D){
                successCount++;
            }
            assertEquals(1, successCount);
            Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
            assertTrue(updatedAccount.getBalance().compareTo(BigDecimal.ZERO)>=0);
            assertTrue(updatedAccount.getBalance().compareTo(new BigDecimal("3000.00"))==0||updatedAccount.getBalance().compareTo(new BigDecimal("5000.00")) ==0);
            System.out.println("Withdrawal 1 done: " +withdrawal1D);
            System.out.println("Withdrawal 2 done: "+withdrawal2D);
            System.out.println("Final balance: "+updatedAccount.getBalance());

        }
        finally{
            start.countDown();
            executor.shutdownNow();
        }
    }
}
