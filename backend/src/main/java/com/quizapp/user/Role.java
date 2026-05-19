package com.quizapp.user;

/**
 * Kullanıcı rolleri.
 * Spring Security'de GrantedAuthority olarak kullanılır.
 *   USER  — normal kullanıcı, quiz çözebilir
 *   ADMIN — yönetici, soru/quiz CRUD + sonuç görüntüleme
 */
public enum Role {
    USER,
    ADMIN
}
