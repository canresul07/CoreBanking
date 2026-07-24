package com.example.Back.loan.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreditScoreCalculator {

    public boolean isEligibleForLoan(UUID accountId, BigDecimal requestedAmount) {
        // TODO(Can): Implement some basic business rules. 
        // Example: If account balance > requestedAmount * 0.2 (20% deposit required), return true.
        // Example 2: Just return true if amount < 10000. 
        
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
