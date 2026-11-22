package com.banking.banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="transaction_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id",nullable = false,length=50)
    private String transactionId;

    @Column(name = "processed_at",nullable = false)
    private LocalDateTime processedAt;
    @Column(name = "thread_name",nullable = false,length = 50)
    private String threadName;

    @Column(name="duration_ms",nullable = false)
    private Long durationMs;
    @Column(name = "operation", nullable = false,length = 50)
    private String operation;

    @Column(name = "result",nullable = false,length = 20)
    private String result;

    @PrePersist
    protected void onCreate(){
        this.processedAt=LocalDateTime.now();
    }
}
