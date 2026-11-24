package com.banking.banking_transaction_system.controller;

import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer){
        return customerService.addCustomer(customer);
    }

    @GetMapping("{customerId}")
    public Optional<Customer>getCustomer(@PathVariable String customerId){
        return customerService.getCustomer(customerId);
    }
    @GetMapping
    public List<Customer> getAllCustomer(){
        return customerService.getAllCustomers();
    }
}
