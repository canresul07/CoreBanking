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

import com.example.Back.card.repository.CardRepository;
import com.example.Back.loan.repository.LoanRepository;
import com.example.Back.loan.entity.LoanStatus;
import org.springframework.security.core.GrantedAuthority;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventRepository transactionEventRepository;
    private final CardRepository cardRepository;
    private final LoanRepository loanRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", isAdmin ? "ADMIN" : "USER");

        if (isAdmin) {
            long totalUsers = userRepository.count();
            long pendingVirtualCards = cardRepository.countByStatus("PENDING");
            long pendingLoans = loanRepository.countByStatus(LoanStatus.PENDING);
            long totalTransactions = transactionEventRepository.count();

            data.put("totalUsers", totalUsers);
            data.put("pendingVirtualCards", pendingVirtualCards);
            data.put("pendingLoans", pendingLoans);
            data.put("totalTransactions", totalTransactions);
            
            // Stats for Admin Charts
            Map<String, Long> systemStats = new HashMap<>();
            systemStats.put("Kullanıcılar", totalUsers);
            systemStats.put("Bekleyen Kartlar", pendingVirtualCards);
            systemStats.put("Bekleyen Krediler", pendingLoans);
            data.put("systemStats", systemStats);
        } else {
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
                        
                        String type = "UNKNOWN";
                        if (t.getFromAccountId() == null) {
                            type = "ATM_DEPOSIT";
                        } else if (t.getToAccountId() == null) {
                            type = "ATM_WITHDRAW";
                        } else if (accountIds.contains(t.getFromAccountId())) {
                            type = "TRANSFER_OUT";
                        } else {
                            type = "TRANSFER_IN";
                        }
                        map.put("type", type);
                        
                        return map;
                    })
                    .collect(Collectors.toList());

            data.put("totalBalance", totalBalance);
            data.put("recentTransactions", recentTransactions);
            
            List<Map<String, Object>> accountStats = accounts.stream().map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("accountName", a.getAccountNumber());
                map.put("balance", a.getBalance());
                return map;
            }).collect(Collectors.toList());
            data.put("accountStats", accountStats);
        }

        return ResponseEntity.ok(data);
    }
}
