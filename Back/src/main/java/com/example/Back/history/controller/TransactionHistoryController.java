package com.example.Back.history.controller;

import com.example.Back.history.entity.TransactionEvent;
import com.example.Back.history.repository.TransactionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionEventRepository historyRepository;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionEvent>> getHistory(@PathVariable UUID accountId) {
        // Hem gönderen hem alan olduğu tüm transfer geçmişini getir.
        return ResponseEntity.ok(historyRepository.findByFromAccountIdOrToAccountIdOrderByTimestampDesc(accountId, accountId));
    }
}
