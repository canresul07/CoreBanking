package com.example.Back.account.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private String currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public static AccountResponse from(com.example.Back.account.entity.Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
