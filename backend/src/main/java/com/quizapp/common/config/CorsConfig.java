package com.quizapp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) yapılandırması.
 *
 * Geliştirme ortamında frontend (Vite :5173) ve üretimde Caddy (:80)
 * farklı origin'lerden backend'e istek gönderir.
 * Bu yapılandırma bu isteklere izin verir.
 *
 * Üretimde allowedOriginPatterns yerine kesin domain kullanın.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Tüm origin'lere izin ver (geliştirme + Docker içi Caddy proxy)
        config.setAllowedOriginPatterns(List.of("*"));

        // İzin verilen HTTP metodları
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // İzin verilen başlıklar (Authorization: Bearer <token> için gerekli)
        config.setAllowedHeaders(List.of("*"));

        // Cookie / Authorization başlığı taşıyan isteklere izin ver
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
