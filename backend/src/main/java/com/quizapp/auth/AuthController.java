package com.quizapp.auth;

import com.quizapp.auth.dto.AuthResponse;
import com.quizapp.auth.dto.LoginRequest;
import com.quizapp.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kimlik doğrulama REST endpoint'leri.
 *
 * SecurityConfig'de /api/auth/** herkese açık olarak tanımlandığı için
 * bu controller'a token gerekmeden erişilebilir.
 *
 * POST /api/auth/register → yeni kullanıcı kaydı
 * POST /api/auth/login    → giriş, JWT döner
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Yeni kullanıcı kaydı — 201 Created döner */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    /** Kullanıcı girişi — 200 OK + JWT döner */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
