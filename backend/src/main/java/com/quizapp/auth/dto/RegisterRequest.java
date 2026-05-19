package com.quizapp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Kayıt isteği DTO — frontend'den gelen JSON bu nesneye dönüştürülür */
@Data
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır")
    private String username;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Parola boş olamaz")
    @Size(min = 6, message = "Parola en az 6 karakter olmalıdır")
    private String password;
}
