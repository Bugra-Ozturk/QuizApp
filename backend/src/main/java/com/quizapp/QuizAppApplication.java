package com.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuizApp — Giriş noktası
 *
 * @SpringBootApplication şu anotasyonları birleştirir:
 *   - @Configuration      : Bu sınıf Spring Bean tanımlarına ev sahipliği yapabilir
 *   - @EnableAutoConfiguration : Classpath'e göre Spring Boot otomatik yapılandırmasını etkinleştirir
 *   - @ComponentScan      : com.quizapp paketi ve alt paketlerini tarar
 */
@SpringBootApplication
public class QuizAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizAppApplication.class, args);
    }
}
