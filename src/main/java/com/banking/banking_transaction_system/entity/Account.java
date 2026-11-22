
package com.banking.banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account{

    @Id
    @Column(name="account_id", nullable = false, length = 20)
    private String accountId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_id",nullable = false)
    private Customer customer;

    @Column(name="account_type", nullable = false, length=20)
    private String accountType;

    @Column(name="balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name="status", nullable = false, length = 10)
    private String status;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
