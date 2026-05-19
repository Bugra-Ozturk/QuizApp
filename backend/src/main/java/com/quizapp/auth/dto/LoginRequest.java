package com.quizapp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Giriş isteği DTO */
@Data
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;

    @NotBlank(message = "Parola boş olamaz")
    private String password;
}
