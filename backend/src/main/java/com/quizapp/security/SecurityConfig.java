package com.quizapp.security;

import com.quizapp.common.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security merkezi yapılandırması.
 *
 * ── Temel Kararlar ───────────────────────────────────────────────────────────
 *
 * 1) CSRF devre dışı:
 *    JWT stateless olduğu için sunucu tarafında oturum tutulmaz.
 *    CSRF saldırısı session cookie'ye dayanır; JWT'de geçerli değildir.
 *
 * 2) SessionCreationPolicy.STATELESS:
 *    Spring Security hiçbir HttpSession oluşturmaz.
 *    Her istek kendi JWT'siyle doğrulanır.
 *
 * 3) Endpoint yetki kuralları (requestMatchers):
 *    PUBLIC    → /api/auth/**          (kayıt, giriş)
 *    PUBLIC    → /api/categories GET   (quiz listesi için)
 *    PUBLIC    → /api/quizzes GET      (quiz listesi için)
 *    USER+     → /api/attempts/**      (quiz çözme, geçmiş)
 *    USER+     → /api/leaderboard/**
 *    ADMIN     → /api/admin/**         (CRUD işlemleri)
 *    Diğerleri → authenticate olmayı gerektir
 *
 * 4) @EnableMethodSecurity:
 *    @PreAuthorize("hasRole('ADMIN')") gibi metot düzeyinde güvenlik aktif.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // @PreAuthorize anotasyonlarını etkinleştirir
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfig corsConfig;

    /**
     * Güvenlik filtre zinciri.
     * HTTP istekleri bu zincirden geçer; her filtre sırasıyla çalışır.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS yapılandırmasını CorsConfig bean'ından al
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

            // CSRF devre dışı (JWT stateless olduğu için gerekmez)
            .csrf(AbstractHttpConfigurer::disable)

            // Endpoint yetki kuralları
            .authorizeHttpRequests(auth -> auth
                // Kimlik doğrulama endpoint'leri herkese açık
                .requestMatchers("/api/auth/**").permitAll()

                // Kategori ve quiz listeleme herkese açık
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/quizzes/**").permitAll()

                // Admin endpoint'leri yalnızca ADMIN rolüne açık
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Diğer tüm istekler için kimlik doğrulama zorunlu
                .anyRequest().authenticated()
            )

            // Stateless oturum politikası (JWT için zorunlu)
            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Kimlik doğrulama sağlayıcısını kaydet
            .authenticationProvider(authenticationProvider())

            // JWT filtresini UsernamePasswordAuthenticationFilter'dan ÖNCE çalıştır
            // Böylece her istek JWT ile doğrulanır, form login yerine
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * DaoAuthenticationProvider:
     * - UserDetailsService: kullanıcıyı veritabanından yükler
     * - PasswordEncoder: girilen parolayı BCrypt hash ile karşılaştırır
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * BCryptPasswordEncoder: endüstri standardı parola hashleme.
     * Strength 10 (varsayılan): her hash işlemi ~100ms sürer,
     * brute-force saldırılarını yavaşlatır.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager: AuthService'deki authenticate() çağrısı için gerekli.
     * Spring Boot'un varsayılan yapılandırmasından alınır.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
