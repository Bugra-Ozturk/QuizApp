package com.quizapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT kimlik doğrulama filtresi.
 *
 * ── Çalışma Akışı ────────────────────────────────────────────────────────────
 *
 *   HTTP İsteği
 *       │
 *       ▼
 *   Authorization başlığı var mı? ("Bearer <token>")
 *       │ Hayır → filtreyi atla (public endpoint'ler geçer)
 *       │ Evet
 *       ▼
 *   Token'dan kullanıcı adını çıkar (JwtService.extractUsername)
 *       │
 *       ▼
 *   SecurityContext'te zaten kimlik doğrulanmış kullanıcı var mı?
 *       │ Evet → filtreyi atla (çift işlem önlenir)
 *       │ Hayır
 *       ▼
 *   Kullanıcıyı veritabanından yükle (UserDetailsService)
 *       │
 *       ▼
 *   Token geçerli mi? (imza + süre)
 *       │ Hayır → 401 (Spring Security tarafından işlenir)
 *       │ Evet
 *       ▼
 *   SecurityContext'e kimlik doğrulama nesnesi koy
 *       │
 *       ▼
 *   Sonraki filtre / controller'a devam et
 *
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * OncePerRequestFilter: Her HTTP isteğinde yalnızca bir kez çalışır.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) Authorization başlığını oku
        final String authHeader = request.getHeader("Authorization");

        // 2) Başlık yoksa veya "Bearer " ile başlamıyorsa filtreyi geç
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) "Bearer " önekini kaldır, token'ı al
        final String jwt = authHeader.substring(7);

        // 4) Token'dan kullanıcı adını çıkar (geçersizse exception → 401)
        final String username = jwtService.extractUsername(jwt);

        // 5) Kullanıcı adı varsa VE henüz authenticate edilmemişse devam et
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6) Kullanıcıyı veritabanından yükle
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7) Token geçerliyse SecurityContext'i güncelle
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // UsernamePasswordAuthenticationToken: Spring Security'nin standart auth nesnesi
                // credentials=null çünkü parola artık gerekmez (token zaten doğrulandı)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // İstek detaylarını (IP, session) authentication nesnesine ekle
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext'e koy — bu noktadan itibaren kullanıcı "authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8) Filtre zincirinin devamına geç
        filterChain.doFilter(request, response);
    }
}
