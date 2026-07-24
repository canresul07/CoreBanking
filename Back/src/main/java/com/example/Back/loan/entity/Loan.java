package com.example.Back.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // TODO(Can): Add accountId (UUID) field to associate loan with an account
    // TODO(Can): Add amount (BigDecimal) field for loan amount
    // TODO(Can): Add interestRate (BigDecimal) field
    // TODO(Can): Add status (LoanStatus) enum field
    // TODO(Can): Add createdAt and updatedAt fields with proper annotations

    // TODO(Can): Generate Getters and Setters
}
