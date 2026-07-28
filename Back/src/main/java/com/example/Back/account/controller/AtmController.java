package com.example.Back.account.controller;

import com.example.Back.account.entity.Account;
import com.example.Back.account.repository.AccountRepository;
import com.example.Back.auth.entity.User;
import com.example.Back.auth.repository.UserRepository;
import com.example.Back.history.entity.TransactionEvent;
import com.example.Back.history.repository.TransactionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.context.ApplicationEventPublisher;
import com.example.Back.account.event.AtmTransactionEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/atm")
@RequiredArgsConstructor
public class AtmController {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventRepository transactionEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(Authentication authentication, @RequestBody Map<String, Object> req) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String accountIdStr = (String) req.get("accountId");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());

        Account account = accountRepository.findById(java.util.UUID.fromString(accountIdStr))
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));
        
        if (!account.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        TransactionEvent event = TransactionEvent.builder()
                .fromAccountId(null) // ATM
                .toAccountId(account.getId())
                .amount(amount)
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();
        transactionEventRepository.save(event);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Para yatırma işlemi başarılı.");
        resp.put("newBalance", account.getBalance());
        
        eventPublisher.publishEvent(new AtmTransactionEvent(this, account.getId(), amount, "DEPOSIT"));
        
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(Authentication authentication, @RequestBody Map<String, Object> req) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String accountIdStr = (String) req.get("accountId");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());

        Account account = accountRepository.findById(java.util.UUID.fromString(accountIdStr))
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));
        
        if (!account.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        if (account.getBalance().compareTo(amount) < 0) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Yetersiz bakiye.");
            return ResponseEntity.badRequest().body(err);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        TransactionEvent event = TransactionEvent.builder()
                .fromAccountId(account.getId())
                .toAccountId(null) // ATM
                .amount(amount)
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();
        transactionEventRepository.save(event);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Para çekme işlemi başarılı.");
        resp.put("newBalance", account.getBalance());
        
        eventPublisher.publishEvent(new AtmTransactionEvent(this, account.getId(), amount, "WITHDRAW"));
        
        return ResponseEntity.ok(resp);
    }
}
