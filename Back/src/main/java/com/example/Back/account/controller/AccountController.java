package com.example.Back.account.controller;

import com.example.Back.account.dto.AccountCreateRequest;
import com.example.Back.account.dto.AccountResponse;
import com.example.Back.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // TODO(Can): Normalde userId, JWT token içinden @AuthenticationPrincipal ile alınır.
    // Şimdilik test edebilmen için bir header'dan (X-User-Id) alıyoruz.
    // İleride Security Context tam oturduğunda burayı token'dan okuyacak şekilde güncelleyeceğiz.

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestHeader("X-User-Id") UUID userId,
                                                         @Valid @RequestBody AccountCreateRequest request) {
        return ResponseEntity.ok(accountService.createAccount(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getBalance(accountId));
    }
}
