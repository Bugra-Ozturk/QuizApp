package com.quizapp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User entity için veri erişim katmanı.
 * JpaRepository temel CRUD operasyonlarını (save, findById, findAll, delete) sağlar.
 * Spring Data JPA, method imzasından SQL sorgusunu otomatik üretir.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Kullanıcı adına göre arama — giriş ve JWT doğrulamasında kullanılır */
    Optional<User> findByUsername(String username);

    /** E-posta tekrar kaydı önlemek için kayıt sırasında kontrol */
    boolean existsByEmail(String email);

    /** Kullanıcı adı tekrar kaydı önlemek için kayıt sırasında kontrol */
    boolean existsByUsername(String username);
}
