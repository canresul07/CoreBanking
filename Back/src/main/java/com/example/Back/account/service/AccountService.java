package com.example.Back.account.service;

import com.example.Back.account.dto.AccountCreateRequest;
import com.example.Back.account.dto.AccountResponse;
import com.example.Back.account.entity.Account;
import com.example.Back.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final StringRedisTemplate redisTemplate;

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public AccountResponse createAccount(UUID userId, AccountCreateRequest request) {

        String accountNumber = generateAccountNumber();
        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account);
        return AccountResponse.from(account);
        // TODO(Can): Yeni bir hesap oluşturma mantığını yaz:
        // 1. Benzersiz bir hesap numarası (accountNumber) üret (Örn: Rastgele 10 haneli
        // sayı veya UUID'nin bir kısmı).
        // 2. Account entity'sini oluştur (Builder ile userId, accountNumber, currency
        // set et, balance 0 olsun).
        // 3. Veritabanına kaydet.
        // 4. Kaydedilen entity'yi AccountResponse'a çevirip döndür.

    }

    public List<AccountResponse> getUserAccounts(UUID userId) {

        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(AccountResponse::from)
                .toList();

        // TODO(Can): Kullanıcının hesaplarını getir:

        // 1. AccountRepository üzerinden kullanıcının hesaplarını çek.
        // 2. Stream kullanarak herbirini AccountResponse'a maple ve List olarak dön.
        // return List.of();
    }

    public BigDecimal getBalance(UUID accountId) {
        String balanceStr = redisTemplate.opsForValue().get("account:balance:" + accountId);
        if (balanceStr != null) {
            return new BigDecimal(balanceStr);
        }

        Account account = accountRepository.findById(accountId).orElseThrow();
        redisTemplate.opsForValue().set("account:balance:" + accountId, account.getBalance().toString(), 10,
                TimeUnit.MINUTES);
        return account.getBalance();
    }
}
