package com.example.Back.card.event;

import lombok.Getter;
import java.util.UUID;

@Getter
public class CardApprovedEvent {
    private final UUID userId;
    private final String cardNumber;

    public CardApprovedEvent(Object source, UUID userId, String cardNumber) {
        this.userId = userId;
        this.cardNumber = cardNumber;
    }
}
