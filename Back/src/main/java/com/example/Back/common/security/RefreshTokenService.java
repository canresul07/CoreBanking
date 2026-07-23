package com.example.Back.common.security;

import java.time.Duration;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public String createRefreshToken(String userId) {

        String token = UUID.randomUUID().toString();
        String key = "refresh:" + userId;
        redisTemplate.opsForValue().set(key, token, Duration.ofDays(7));
        return token;

        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // String token = UUID.randomUUID().toString(); // 1. adım
        // String key = "refresh:" + userId; // 2. adım
        // redisTemplate.opsForValue().set(key, token, Duration.ofDays(7)); // 3. adım
        // return token; // 4. adım
        //
        // TODO(Can): 1. UUID.randomUUID().toString() ile yeni bir rastgele token
        // string'i üret.

        // TODO(Can): 2. Redis'te saklanacak key'i oluştur (örn. "refresh:" + userId).

        // TODO(Can): 3. redisTemplate.opsForValue().set(key, token, Duration.ofDays(7))
        // ile token'ı 7 gün süreli olarak Redis'e kaydet.

        // TODO(Can): 4. Üretilen token string'ini return et.

    }

    public boolean validateRefreshToken(String userId, String token) {

        String storedToken = redisTemplate.opsForValue().get("refresh:" + userId);
        return storedToken != null && storedToken.equals(token);
        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // String storedToken = redisTemplate.opsForValue().get("refresh:" + userId); //
        // 1. adım
        // return storedToken != null && storedToken.equals(token); // 2. adım
        //
        // TODO(Can): 1. Redis'ten "refresh:" + userId key'i ile kayıtlı token'ı oku.

        // TODO(Can): 2. Okunan token boş değilse ve parametre olarak gelen 'token' ile
        // eşleşiyorsa true dön, aksi halde false dön.

    }

    public void deleteRefreshToken(String userId) {

        redisTemplate.delete("refresh:" + userId);
        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // redisTemplate.delete("refresh:" + userId); // 1. adım
        //
        // TODO(Can): redisTemplate.delete(...) metodunu kullanarak kullanıcının refresh
        // token'ını sil (logout işlemi için).
    }
}
