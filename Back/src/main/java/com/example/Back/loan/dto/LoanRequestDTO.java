package com.example.Back.loan.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanRequestDTO {
    private UUID accountId;
    private BigDecimal amount;

    // Getters and Setters
    public UUID getAccountId() {
        return accountId;
    }
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
