package com.example.Back.common.config;

import com.example.Back.common.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 12 strength ile oluşturulur (Bölüm 5.2'deki güvenlik
        // standardı)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // http
        // .csrf(csrf -> csrf.disable()) // 1. adım
        // .sessionManagement(session ->
        // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 2. adım
        // .authorizeHttpRequests(auth -> auth // 3. adım
        // .requestMatchers("/api/auth/**").permitAll() // 3a. adım
        // .anyRequest().authenticated() // 3b. adım
        // )
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // // 4. adım
        // return http.build();
        //
        // TODO(Can): 1. http.csrf(csrf -> csrf.disable()) ile şimdilik CSRF'i kapat
        // (geliştirme aşaması için).

        // TODO(Can): 2. sessionManagement'ı STATELESS olarak ayarla
        // (SessionCreationPolicy.STATELESS). Çünkü JWT kullanıyoruz.

        // TODO(Can): 3. authorizeHttpRequests bloğunu yapılandır:
        // a. "/api/auth/**" (login, register vb.) uç noktalarına herkesin erişebilmesi
        // için .permitAll() de.
        // b. Diğer tüm istekler (.anyRequest()) için yetki iste (.authenticated()).

        // TODO(Can): 4. jwtAuthFilter'ı UsernamePasswordAuthenticationFilter.class'tan
        // ÖNCE çalışacak şekilde ekle (addFilterBefore).

        return http.build();
    }
}
