package com.quizapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Başarılı kimlik doğrulama yanıtı — frontend bu nesneyi alır ve token'ı saklar */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
