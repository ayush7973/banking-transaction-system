package com.banking.banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_transaction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_id",nullable = false,length = 50)
    private String transactionId;

    @Column(name = "error_category",nullable = false,length = 50)
    private String errorCategory;

    @Column(name = "error_message",nullable = false,length = 500)
    private String errorMessage;
    @Column(name = "failed_at",nullable = false)
    private LocalDateTime failedAt;

    @PrePersist
    protected void onCreate(){
        this.failedAt=LocalDateTime.now();
    }
}
