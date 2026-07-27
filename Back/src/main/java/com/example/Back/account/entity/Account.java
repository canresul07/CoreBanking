package com.example.Back.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO(Can): Bu sınıf bir JPA Entity'si olacak (veritabanı tablosu karşılığı). 
// 1. @Entity ve @Table anotasyonlarını ekle (tablo adı "accounts" olsun).
// 2. Lombok anotasyonlarını ekle (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder).
// 3. Entity için gerekli alanları tanımla:
//    - id (UUID türünde, Primary Key, otomatik oluşturulmalı)
//    - userId (UUID türünde, accounts tablosunun users tablosu ile ilişkisini temsil edecek - şimdilik sadece sütun olarak tutabilirsin)
//    - accountNumber (String, benzersiz (unique) ve nullable=false olmalı)
//    - currency (String, nullable=false)
//    - balance (BigDecimal, varsayılan 0.00 olmalı)
//    - createdAt (LocalDateTime, @CreationTimestamp)
//    - updatedAt (LocalDateTime, @UpdateTimestamp)

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
