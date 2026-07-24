package com.example.Back.transfer.service;

import com.example.Back.transfer.dto.TransferCreateRequest;
import com.example.Back.transfer.dto.TransferResponse;
import com.example.Back.transfer.entity.Transfer;
import com.example.Back.transfer.repository.TransferRepository;
import com.example.Back.account.repository.AccountRepository;
import com.example.Back.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final DistributedLockService lockService;
    private final StringRedisTemplate redisTemplate;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransferResponse executeTransfer(TransferCreateRequest request) {

        String idempotencyKey = request.getIdempotencyKey();

        Optional<Transfer> existingTransfer = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTransfer.isPresent()) {
            return TransferResponse.from(existingTransfer.get());
        }

        String lockKey = "lock:account:" + request.getFromAccountId();

        if (!lockService.acquireLock(lockKey, 5)) {
            throw new RuntimeException("Sistem meşgul, aynı hesaptan işlem yapılıyor");
        }

        try {
            Optional<Account> fromAccountOpt = accountRepository.findById(request.getFromAccountId());
            Optional<Account> toAccountOpt = accountRepository.findById(request.getToAccountId());

            if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
                throw new RuntimeException("Hesap bulunamadı");
            }

            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();

            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Yetersiz bakiye");
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            Transfer transfer = Transfer.builder()
                    .idempotencyKey(idempotencyKey)
                    .fromAccountId(request.getFromAccountId())
                    .toAccountId(request.getToAccountId())
                    .amount(request.getAmount())
                    .status("COMPLETED")
                    .build();

            transferRepository.save(transfer);

            redisTemplate.delete("account:balance:" + fromAccount.getId());
            redisTemplate.delete("account:balance:" + toAccount.getId());

            eventPublisher.publishEvent(new com.example.Back.history.event.TransferCompletedEvent(transfer));

            return TransferResponse.from(transfer);
        } finally {
            lockService.releaseLock(lockKey);
        }
    }
}
