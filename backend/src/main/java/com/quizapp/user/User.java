package com.quizapp.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Kullanıcı entity'si — veritabanındaki "users" tablosuna karşılık gelir.
 *
 * UserDetails arayüzü Spring Security ile entegrasyon sağlar:
 *   getUsername()      → kimlik doğrulamada kullanılan benzersiz alan (burada: username)
 *   getPassword()      → BCrypt ile hashlenmiş parola
 *   getAuthorities()   → kullanıcının rolleri (ROLE_USER, ROLE_ADMIN)
 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Giriş yaparken kullanılan benzersiz kullanıcı adı */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** İletişim için e-posta (benzersiz) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCryptPasswordEncoder ile hashlenmiş parola — asla düz metin saklanmaz */
    @Column(nullable = false)
    private String password;

    /** Kullanıcının rolü: USER veya ADMIN */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    /** Hesap oluşturulma zamanı (otomatik atanır) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Spring Security UserDetails implementasyonu ─────────────────────────

    /**
     * Kullanıcının rolünü "ROLE_" önekiyle Spring Security'ye iletir.
     * Örn: Role.ADMIN → "ROLE_ADMIN"
     * Bu sayede @PreAuthorize("hasRole('ADMIN')") çalışır.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Hesap süresi dolmamış (üretimde genişletilebilir) */
    @Override public boolean isAccountNonExpired()     { return true; }
    /** Hesap kilitli değil */
    @Override public boolean isAccountNonLocked()      { return true; }
    /** Kimlik bilgileri geçerli */
    @Override public boolean isCredentialsNonExpired() { return true; }
    /** Hesap aktif */
    @Override public boolean isEnabled()               { return true; }
}
