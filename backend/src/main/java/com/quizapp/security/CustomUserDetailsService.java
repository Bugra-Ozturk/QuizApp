package com.quizapp.security;

import com.quizapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security'nin kullanıcı yükleme arayüzünü uygular.
 *
 * JwtAuthFilter her doğrulamada bu servisi çağırır:
 *   1. Token'dan username çıkarılır
 *   2. Bu servis username ile veritabanından User'ı getirir
 *   3. User nesnesi (UserDetails) SecurityContext'e yerleştirilir
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Kullanıcı adına göre veritabanından kullanıcıyı yükler.
     *
     * @throws UsernameNotFoundException kullanıcı bulunamazsa —
     *         Spring Security bu exception'ı 401 yanıtına çevirir
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Kullanıcı bulunamadı: " + username));
    }
}
