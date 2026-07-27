package com.example.Back.card.service;

import com.example.Back.auth.entity.User;
import com.example.Back.auth.repository.UserRepository;
import com.example.Back.card.dto.CardDTO;
import com.example.Back.card.entity.Card;
import com.example.Back.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public void createPhysicalCardForUser(User user) {
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(generateCardNumber());
        card.setCardholderName(user.getUsername().toUpperCase());
        card.setExpirationDate(generateExpirationDate());
        card.setCvv(generateCvv());
        card.setCardType("PHYSICAL");
        card.setStatus("ACTIVE");
        card.setLimitAmount(new BigDecimal("10000.00"));
        cardRepository.save(card);
    }

    public CardDTO requestVirtualCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(generateCardNumber());
        card.setCardholderName(user.getUsername().toUpperCase());
        card.setExpirationDate(generateExpirationDate());
        card.setCvv(generateCvv());
        card.setCardType("VIRTUAL");
        card.setStatus("PENDING");
        card.setLimitAmount(new BigDecimal("5000.00"));
        
        card = cardRepository.save(card);
        return mapToDTO(card);
    }

    public List<CardDTO> getMyCards(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        return cardRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CardDTO> getPendingCards() {
        return cardRepository.findByStatus("PENDING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void approveCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Kart bulunamadı"));
        card.setStatus("ACTIVE");
        cardRepository.save(card);
    }

    public void rejectCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Kart bulunamadı"));
        card.setStatus("REJECTED");
        cardRepository.save(card);
    }

    public void deleteCard(UUID cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Kart bulunamadı"));
        
        if (!card.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bu kartı silme yetkiniz yok");
        }
        
        if ("PHYSICAL".equals(card.getCardType())) {
            throw new RuntimeException("Fiziksel kartlar silinemez");
        }

        cardRepository.delete(card);
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder("4532");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateExpirationDate() {
        LocalDate expiry = LocalDate.now().plusYears(4);
        return expiry.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String generateCvv() {
        return String.format("%03d", random.nextInt(1000));
    }

    private CardDTO mapToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setCardholderName(card.getCardholderName());
        dto.setExpirationDate(card.getExpirationDate());
        dto.setCvv(card.getCvv());
        dto.setCardType(card.getCardType());
        dto.setStatus(card.getStatus());
        dto.setLimitAmount(card.getLimitAmount());
        return dto;
    }
}
