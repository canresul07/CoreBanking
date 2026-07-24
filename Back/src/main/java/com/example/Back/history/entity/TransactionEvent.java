package com.example.Back.history.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document(collection = "transaction_history")
public class TransactionEvent {
    @Id
    private String id; // MongoDB otomatik String ID (ObjectId) atar
    
    private UUID transferId;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String status;
    private String idempotencyKey;
    private LocalDateTime timestamp;
}
