package com.example.Back.common.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    // TODO(Can): UserDetailsService'i import edip inject etmeniz gerekebilir (kullanıcıyı veritabanından yüklemek için).

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ÖRNEK ŞABLON (Aşağıdaki adımlara tam uyumlu syntax örneği):
        // String header = request.getHeader("Authorization");                           // 1. adım
        // if (header == null || !header.startsWith("Bearer ")) {                        // 2. adım
        //     filterChain.doFilter(request, response);                                  // 2. adım
        //     return;                                                                   // 2. adım
        // }
        // String token = header.substring(7);                                           // 3. adım
        // if (!jwtTokenProvider.validateToken(token)) {                                 // 4. adım
        //     filterChain.doFilter(request, response);                                  // 4. adım
        //     return;                                                                   // 4. adım
        // }
        // String username = jwtTokenProvider.getUsernameFromToken(token);               // 5. adım
        // if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // 6. adım
        //     UserDetails userDetails = userDetailsService.loadUserByUsername(username);            // 6a
        //     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( // 6b
        //             userDetails, null, userDetails.getAuthorities());                              // 6b
        //     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));      // 6c
        //     SecurityContextHolder.getContext().setAuthentication(authToken);                       // 6d
        // }
        //
        // TODO(Can): 1. Gelen HTTP isteğinden (HttpServletRequest) "Authorization" header'ını oku.
        
        // TODO(Can): 2. Header var mı ve "Bearer " ile mi başlıyor kontrol et. Değilse filterChain.doFilter(request, response) yap ve metottan çık.
        
        // TODO(Can): 3. "Bearer " kısmını atıp sadece token string'ini al (substring).
        
        // TODO(Can): 4. jwtTokenProvider.validateToken(token) metodunu çağırarak token geçerli mi kontrol et. Geçersizse filterChain.doFilter(...) yapıp çık.
        
        // TODO(Can): 5. jwtTokenProvider.getUsernameFromToken(token) ile kullanıcı adını al.
        
        // TODO(Can): 6. SecurityContextHolder.getContext().getAuthentication() null ise (yani henüz auth olmamışsa):
        //    a. UserDetailsService ile username'e göre kullanıcıyı (UserDetails) yükle.
        //    b. UsernamePasswordAuthenticationToken objesi oluştur (parametreler: userDetails, null, userDetails.getAuthorities()).
        //    c. authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)) ile istek detaylarını ekle.
        //    d. SecurityContextHolder.getContext().setAuthentication(authToken) ile Spring Security context'ine set et.
        
        // TODO(Can): 7. filterChain.doFilter(request, response) ile zincire devam et.
        
        filterChain.doFilter(request, response); // Bu satırı TODO'ları tamamladığında uygun yere taşıyıp silebilirsin.
    }
}
