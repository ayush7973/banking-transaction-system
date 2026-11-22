package com.banking.banking_transaction_system.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Transaction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false,length = 50,unique = true)
    private String transactionId;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @Column(name="amount", nullable = false, precision =15, scale = 2)
    private BigDecimal amount;
    @Column(name="source_account_id",length=20)
    private String sourceAccountId;

    @Column(name="dest_account_id", length = 20)
    private String destAccountId;
    @Column(name="status", nullable = false, length = 20)
    private String status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name="created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
        if(this.status==null){
            this.status="PENDING";
        }
    }
}
