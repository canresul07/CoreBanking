package com.example.Back.loan.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.Back.account.service.AccountService;

@Component
public class CreditScoreCalculator {

    private final AccountService accountService;

    public CreditScoreCalculator(AccountService accountService) {
        this.accountService = accountService;
    }

    public boolean isEligibleForLoan(UUID accountId, BigDecimal requestedAmount) {

        BigDecimal accountBalance = accountService.getBalance(accountId);

        if (accountBalance.compareTo(requestedAmount.multiply(BigDecimal.valueOf(0.2))) > 0) {
            return true;
        }

        if (requestedAmount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            return true;
        }

        // TODO(Can): Implement some basic business rules.
        // Example: If account balance > requestedAmount * 0.2 (20% deposit required),
        // return true.
        // Example 2: Just return true if amount < 10000.

        return false;
    }
}
