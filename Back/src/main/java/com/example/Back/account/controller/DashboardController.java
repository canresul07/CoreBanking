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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventRepository transactionEventRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<UUID> accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());

        // Limit to latest 5 transactions
        List<TransactionEvent> allTransactions = transactionEventRepository.findByFromAccountIdInOrToAccountIdInOrderByTimestampDesc(accountIds, accountIds);
        List<Map<String, Object>> recentTransactions = allTransactions.stream()
                .limit(5)
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", t.getId());
                    map.put("amount", t.getAmount());
                    map.put("status", t.getStatus());
                    map.put("timestamp", t.getTimestamp());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("totalBalance", totalBalance);
        data.put("recentTransactions", recentTransactions);

        return ResponseEntity.ok(data);
    }
}
