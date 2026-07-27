package com.example.Back.auth.controller;

import com.example.Back.card.dto.CardDTO;
import com.example.Back.card.service.CardService;
import com.example.Back.loan.entity.Loan;
import com.example.Back.loan.entity.LoanStatus;
import com.example.Back.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.Back.account.repository.AccountRepository;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CardService cardService;
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/cards/pending")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDTO>> getPendingCards() {
        return ResponseEntity.ok(cardService.getPendingCards());
    }

    @PutMapping("/cards/{id}/approve")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approveCard(@PathVariable UUID id) {
        cardService.approveCard(id);
        return ResponseEntity.ok(Map.of("message", "Sanal kart onaylandı."));
    }

    @PutMapping("/cards/{id}/reject")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectCard(@PathVariable UUID id) {
        cardService.rejectCard(id);
        return ResponseEntity.ok(Map.of("message", "Sanal kart reddedildi."));
    }

    @GetMapping("/loans/pending")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getPendingLoans() {
        // Fetch all pending loans
        List<Loan> pendingLoans = loanRepository.findAll().stream()
                .filter(loan -> LoanStatus.PENDING.equals(loan.getStatus()))
                .collect(Collectors.toList());

        List<Map<String, Object>> dtos = pendingLoans.stream().map(loan -> {
            return Map.<String, Object>of(
                "id", loan.getId(),
                "amount", loan.getAmount(),
                "interestRate", loan.getInterestRate(),
                "status", loan.getStatus().name()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/loans/{id}/approve")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approveLoan(@PathVariable UUID id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Kredi bulunamadı"));
        loan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);

        // Add amount to account balance
        com.example.Back.account.entity.Account account = accountRepository.findById(loan.getAccountId())
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));
        account.setBalance(account.getBalance().add(loan.getAmount()));
        accountRepository.save(account);

        return ResponseEntity.ok(Map.of("message", "Kredi başvurusu onaylandı."));
    }

    @PutMapping("/loans/{id}/reject")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectLoan(@PathVariable UUID id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Kredi bulunamadı"));
        loan.setStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);
        return ResponseEntity.ok(Map.of("message", "Kredi başvurusu reddedildi."));
    }
}
