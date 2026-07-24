package com.example.Back.transfer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO(Can): Bu sınıf bir JPA Entity'si olacak. 
// 1. @Entity ve @Table(name = "transfers") ekle.
// 2. Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder) ekle.
// 3. Sütunlar:
//    - id (UUID, @Id, @GeneratedValue)
//    - fromAccountId (UUID, nullable=false)
//    - toAccountId (UUID, nullable=false)
//    - amount (BigDecimal, nullable=false)
//    - status (String, nullable=false) -> PENDING, COMPLETED, FAILED olabilir
//    - idempotencyKey (String, unique=true, nullable=false) -> Çok ÖNEMLİ: Aynı key ile iki transfer yapılamaz!
//    - createdAt (LocalDateTime, @CreationTimestamp)

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID fromAccountId;

    @Column(nullable = false)
    private UUID toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
