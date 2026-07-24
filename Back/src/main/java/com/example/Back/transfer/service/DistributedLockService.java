package com.example.Back.transfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    // TODO(Can): Redis ile Dağıtık Kilit (Distributed Lock) mantığını yaz!
    // Amacımız: Aynı hesaptan aynı anda iki farklı sunucu/istek para çekmeye
    // çalışırsa bunu engellemek.
    public boolean acquireLock(String lockKey, long timeoutSeconds) {
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(timeoutSeconds));
        return locked != null && locked;
        // 1. redisTemplate.opsForValue().setIfAbsent(...) metodunu kullan.
        // 2. Eğer kilit alınırsa (true dönerse) işlemi kabul et, süre (timeoutSeconds)
        // ver ki kilit sonsuza kadar kalmasın (Örn:
        // Duration.ofSeconds(timeoutSeconds)).
    }

    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
        // 1. redisTemplate.delete(lockKey) ile kilidi sil.

    }
}
