package com.quizapp.auth;

import com.quizapp.auth.dto.AuthResponse;
import com.quizapp.auth.dto.LoginRequest;
import com.quizapp.auth.dto.RegisterRequest;
import com.quizapp.common.exception.ApiException;
import com.quizapp.security.JwtService;
import com.quizapp.user.Role;
import com.quizapp.user.User;
import com.quizapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Kimlik doğrulama iş mantığı.
 *
 * ── Kayıt Akışı ──────────────────────────────────────────────────────────────
 * 1. Kullanıcı adı ve e-posta benzersizliği kontrol edilir
 * 2. Parola BCrypt ile hashlenir (asla düz metin saklanmaz)
 * 3. Kullanıcı USER rolüyle veritabanına kaydedilir
 * 4. JWT üretilir ve yanıt olarak döndürülür
 *
 * ── Giriş Akışı ──────────────────────────────────────────────────────────────
 * 1. AuthenticationManager, kullanıcı adı + parola çiftini doğrular
 *    (BCrypt hash karşılaştırması burada yapılır)
 * 2. Başarılıysa JWT üretilir ve yanıt olarak döndürülür
 * 3. Başarısızsa Spring Security BadCredentialsException fırlatır
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /** Yeni kullanıcı kaydı */
    public AuthResponse register(RegisterRequest request) {
        // Kullanıcı adı benzersizlik kontrolü
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Bu kullanıcı adı zaten kullanılıyor", HttpStatus.CONFLICT);
        }
        // E-posta benzersizlik kontrolü
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Bu e-posta adresi zaten kayıtlı", HttpStatus.CONFLICT);
        }

        // Kullanıcıyı oluştur — parola hashle
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .role(Role.USER)  // yeni kayıtlar her zaman USER rolüyle başlar
                .build();

        userRepository.save(user);

        // JWT üret ve yanıt döndür
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    /** Kullanıcı girişi */
    public AuthResponse login(LoginRequest request) {
        // Spring Security kimlik doğrulamasını tetikle
        // Bu çağrı: UserDetailsService.loadUserByUsername → BCrypt karşılaştırması
        // Başarısızsa BadCredentialsException fırlatılır → GlobalExceptionHandler 401 döner
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Kimlik doğrulama başarılı — kullanıcıyı yükle ve JWT üret
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
