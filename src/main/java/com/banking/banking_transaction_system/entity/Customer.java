
package com.banking.banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer{

    @Id
    @Column(name="customer_id", nullable=false, length=20)
    private String customerId;

    @Column(name="full_name", nullable = false, length=100)
    private String fullName;

    @Column(name="email", nullable=false, length=100, unique=true)
    private String email;

    @Column(name="phone", length=15)
    private String phone;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }
}
