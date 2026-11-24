package com.banking.banking_transaction_system.controller;

import com.banking.banking_transaction_system.dto.DepositRequest;
import com.banking.banking_transaction_system.dto.TransferRequest;
import com.banking.banking_transaction_system.dto.WithdrawalRequest;
import com.banking.banking_transaction_system.entity.Transaction;
import com.banking.banking_transaction_system.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody DepositRequest request){
        return transactionService.deposit(request.getTransactionId(), request.getAccountId(), request.getAmount());
    }
    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody WithdrawalRequest request){
        return transactionService.withdraw(request.getTransactionId(),request.getAccountId(),request.getAmount());
    }
    @PostMapping("/transfer")
    public Transaction transfer(@RequestBody TransferRequest request){
        return transactionService.transfer(request.getTransactionId(),request.getSourceAccountId(),request.getDestAccountId(),request.getAmount());
    }

}
