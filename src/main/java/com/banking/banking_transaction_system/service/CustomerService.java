package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.entity.Customer;
import com.banking.banking_transaction_system.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer){
        if(customerRepository.existsByEmail(customer.getEmail())){
            throw new RuntimeException("Customer  already exist");
        }
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomer(String customerId){
        return customerRepository.findById(customerId);
    }
    public Optional<Customer> getCustomerByEmail(String email){
        return customerRepository.findByEmail(email);
    }
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }


}
