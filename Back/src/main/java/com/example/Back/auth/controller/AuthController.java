package com.example.Back.auth.controller;

import com.example.Back.auth.dto.AuthResponse;
import com.example.Back.auth.dto.LoginRequest;
import com.example.Back.auth.dto.RegisterRequest;
import com.example.Back.auth.service.AuthService;
import com.example.Back.common.security.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Kullanıcı başarıyla kaydedildi.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        
        // TODO(Can): Eğer AuthService login metodu sana userId'yi dönmüyorsa (sadece accessToken dönüyorsa), 
        // User entity'sine ulaşıp ID'sini alman gerekebilir. Login işlemi başarılıysa RefreshToken üretmeliyiz.
        // Bu iskelette basitleştirmek adına şimdilik statik bir işlem gibi gösterdim, kendi mantığına göre uyarla.
        
        // Şimdilik token işlemini temsil etmesi için:
        String refreshToken = refreshTokenService.createRefreshToken("user-id-buraya-gelmeli"); 
        
        // Refresh token'ı httpOnly cookie olarak ayarlıyoruz (Bölüm 5.1)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken != null ? refreshToken : "")
                .httpOnly(true)
                .secure(false) // Local dev için false, prod için true olmalı
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 gün
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
}
