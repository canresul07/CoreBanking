package com.example.Back.account.controller;

import com.example.Back.account.dto.AccountCreateRequest;
import com.example.Back.account.dto.AccountResponse;
import com.example.Back.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import com.example.Back.auth.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    // TODO(Can): Normalde userId, JWT token içinden @AuthenticationPrincipal ile alınır.
    // Şimdilik test edebilmen için bir header'dan (X-User-Id) alıyoruz.
    // İleride Security Context tam oturduğunda burayı token'dan okuyacak şekilde güncelleyeceğiz.

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(Authentication authentication,
                                                         @Valid @RequestBody AccountCreateRequest request) {
        String username = authentication.getName();
        com.example.Back.auth.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return ResponseEntity.ok(accountService.createAccount(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Authentication authentication) {
        String username = authentication.getName();
        com.example.Back.auth.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return ResponseEntity.ok(accountService.getUserAccounts(user.getId()));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getBalance(accountId));
    }
}
