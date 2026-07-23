package com.example.Back.auth.service;

import com.example.Back.auth.dto.AuthResponse;
import com.example.Back.auth.dto.LoginRequest;
import com.example.Back.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // TODO(Can): Gerekli bağımlılıkları (@RequiredArgsConstructor ile) inject et:
    // - UserRepository
    // - PasswordEncoder (şifre hashleme ve kontrolü için)
    // - JwtTokenProvider (access token üretmek için)
    // - RefreshTokenService (refresh token işlemleri için)

    public void register(RegisterRequest request) {
        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // if (userRepository.findByUsername(request.getUsername()).isPresent() ||       // 1. adım
        //     userRepository.findByEmail(request.getEmail()).isPresent()) {             // 1. adım
        //     throw new RuntimeException("Kullanıcı adı veya email zaten kullanılıyor"); // 1. adım
        // }
        // User user = new User();                                                       // 2. adım
        // user.setUsername(request.getUsername());                                      // 2. adım
        // user.setEmail(request.getEmail());                                            // 2. adım
        // user.setPasswordHash(passwordEncoder.encode(request.getPassword()));          // 3. adım
        // user.setRole("CUSTOMER");                                                     // 4. adım
        // user.setFailedLoginAttempts(0);                                               // 4. adım
        // userRepository.save(user);                                                    // 5. adım
        //
        // TODO(Can): 1. request.getUsername() veya request.getEmail() veritabanında var mı kontrol et. Varsa hata fırlat (örn. RuntimeException).
        
        // TODO(Can): 2. Yeni bir User entity'si oluştur. username ve email'i set et.
        
        // TODO(Can): 3. passwordEncoder.encode(request.getPassword()) ile şifreyi hashleyip User objesine set et.
        
        // TODO(Can): 4. role = "CUSTOMER", failedLoginAttempts = 0 olarak ayarla.
        
        // TODO(Can): 5. userRepository.save(user) ile veritabanına kaydet.
    }

    public AuthResponse login(LoginRequest request) {
        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // User user = userRepository.findByUsername(request.getUsername())              // 1. adım
        //         .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));      // 1. adım
        // if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) { // 2. adım
        //     user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);            // 2. adım
        //     userRepository.save(user);                                                 // 2. adım
        //     throw new RuntimeException("Hatalı şifre");                                // 2. adım
        // }
        // user.setFailedLoginAttempts(0);                                               // 3. adım
        // userRepository.save(user);                                                    // 3. adım
        // String token = jwtTokenProvider.generateAccessToken(user.getUsername());      // 4. adım
        // return AuthResponse.builder().accessToken(token).build();                     // 5. adım
        //
        // TODO(Can): 1. userRepository.findByUsername ile kullanıcıyı bul. Bulunamazsa hata fırlat.
        
        // TODO(Can): 2. passwordEncoder.matches(request.getPassword(), user.getPasswordHash()) ile şifreler eşleşiyor mu kontrol et. Eşleşmiyorsa failedLoginAttempts'i artır, kaydet ve hata fırlat.
        
        // TODO(Can): 3. Şifre doğruysa: failedLoginAttempts = 0 yapıp güncelle.
        
        // TODO(Can): 4. jwtTokenProvider.generateAccessToken(user.getUsername()) ile access token üret.
        
        // TODO(Can): 5. AuthResponse objesi oluşturup içine access token'ı koyup return et.
        // Not: Refresh token Controller katmanında cookie olarak eklenecek, burada üretmene gerek yok (veya burada üretip bir Wrapper class ile de dönebilirsin, tasarım sana kalmış. Tavsiyem Controller'da RefreshTokenService çağırmak).
        return null;
    }
}
