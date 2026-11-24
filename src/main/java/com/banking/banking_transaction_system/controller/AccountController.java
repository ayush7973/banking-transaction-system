package com.banking.banking_transaction_system.controller;

import com.banking.banking_transaction_system.entity.Account;
import com.banking.banking_transaction_system.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @PostMapping
    public Account createAccount(@RequestBody Account account){
        return accountService.createAccount(account);

    }

    @GetMapping("/{accountId}")
    public Optional<Account> getAccount(@PathVariable String accountId){
        return accountService.getAccount(accountId);
    }
    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }
    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable String customerId){
        return accountService.getAccountsByCustomer(customerId);
    }
}
