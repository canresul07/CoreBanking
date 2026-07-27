package com.example.Back.card.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CardDTO {
    private UUID id;
    private String cardNumber;
    private String cardholderName;
    private String expirationDate;
    private String cvv;
    private String cardType;
    private String status;
    private BigDecimal limitAmount;
}
