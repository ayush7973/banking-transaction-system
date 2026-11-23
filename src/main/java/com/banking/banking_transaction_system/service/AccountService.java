package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.repository.AccountRepository;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;



@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }
    public Account createAccount(Account account) {
        String customerId = account.getCustomer().getCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not exist"));

        account.setCustomer(customer);
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        if (account.getAccountType() == null) {
            account.setAccountType("SAVINGS");
        }
        return accountRepository.save(account);
    }
    public Optional<Account> getAccount(String accountId){
        return accountRepository.findById(accountId);
    }
    public List<Account>getAccountsByCustomer(String customerId){
        return accountRepository.findByCustomerCustomerId(customerId);
    }
    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }
}
