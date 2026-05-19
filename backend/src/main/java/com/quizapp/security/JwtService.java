package com.quizapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT (JSON Web Token) üretimi ve doğrulamasını yöneten servis.
 *
 * ── JWT Anatomisi ────────────────────────────────────────────────────────────
 * Bir JWT üç bölümden oluşur (Base64URL ile kodlanmış, "." ile ayrılmış):
 *
 *   HEADER.PAYLOAD.SIGNATURE
 *
 *   Header   : {"alg": "HS256", "typ": "JWT"}
 *   Payload  : {"sub": "username", "role": "USER", "iat": ..., "exp": ...}
 *   Signature: HMACSHA256(base64(header) + "." + base64(payload), secretKey)
 *
 * İmza, token'ın değiştirilmediğini garanti eder.
 * Sunucu tarafında herhangi bir oturum saklanmaz (stateless).
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Service
public class JwtService {

    /** application.yml'den gelen imzalama anahtarı */
    @Value("${jwt.secret}")
    private String secret;

    /** Token geçerlilik süresi (ms) — varsayılan 24 saat */
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // ─────────────────────────────────────────────────────────────────────────
    // Token Üretimi
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verilen kullanıcı için JWT üretir.
     *
     * Payload'a ek claim olarak kullanıcı rolü eklenir.
     * Bu rol frontend'de ve backend'deki yetki kontrollerinde kullanılır.
     *
     * @param userDetails Spring Security kullanıcı nesnesi
     * @return imzalı JWT string'i
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        // Rol bilgisini token'a göm — frontend menü görünürlüğü için kullanır
        extraClaims.put("role", userDetails.getAuthorities()
                .iterator().next().getAuthority().replace("ROLE_", ""));

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())           // "sub" claim: kullanıcı adı
                .issuedAt(new Date())                         // "iat" claim: oluşturulma zamanı
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // "exp" claim
                .signWith(getSigningKey())                    // HMAC-SHA256 imzası
                .compact();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Token Doğrulama
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Token'ın verilen kullanıcıya ait ve süresi geçmemiş olduğunu doğrular.
     *
     * @param token       doğrulanacak JWT
     * @param userDetails veritabanından yüklenen kullanıcı
     * @return token geçerliyse true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /** Token'dan kullanıcı adını ("sub" claim) çıkarır */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Yardımcı Metotlar
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /** Herhangi bir claim'i token'dan çıkarmak için genel amaçlı metot */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'ı imzalama anahtarıyla doğrulayarak tüm claim'leri parse eder.
     * İmza geçersizse veya token süresi dolduysa JwtException fırlatılır.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Konfigürasyondaki secret string'inden HMAC-SHA imzalama anahtarı üretir.
     * Key en az 256 bit (32 karakter) uzunluğunda olmalıdır.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
