package com.example.Back.account.event;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class AtmTransactionEvent {
    private final UUID accountId;
    private final BigDecimal amount;
    private final String type; // "DEPOSIT" or "WITHDRAW"

    public AtmTransactionEvent(Object source, UUID accountId, BigDecimal amount, String type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
    }
}
