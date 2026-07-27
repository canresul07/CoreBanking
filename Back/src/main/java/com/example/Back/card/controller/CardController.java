package com.example.Back.card.controller;

import com.example.Back.card.dto.CardDTO;
import com.example.Back.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/virtual")
    public ResponseEntity<Map<String, String>> requestVirtualCard(Authentication authentication) {
        cardService.requestVirtualCard(authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Sanal kart talebiniz başarıyla alındı ve onaya gönderildi."));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CardDTO>> getMyCards(Authentication authentication) {
        return ResponseEntity.ok(cardService.getMyCards(authentication.getName()));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId, Authentication authentication) {
        cardService.deleteCard(cardId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
