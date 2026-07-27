package com.example.Back.card.entity;

import com.example.Back.auth.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number", unique = true, nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "cardholder_name", nullable = false, length = 100)
    private String cardholderName;

    @Column(name = "expiration_date", nullable = false, length = 5)
    private String expirationDate;

    @Column(nullable = false, length = 3)
    private String cvv;

    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType; // PHYSICAL or VIRTUAL

    @Column(nullable = false, length = 20)
    private String status; // PENDING, ACTIVE, REJECTED, FROZEN

    @Column(name = "limit_amount")
    private BigDecimal limitAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
