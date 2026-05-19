package com.quizapp.question.dto;

import com.quizapp.question.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Soru oluşturma/güncelleme isteği DTO.
 *
 * CRUD İşlemi Detayı (Ödev Açıklaması):
 *   CREATE: POST /api/admin/questions  → bu DTO request body olarak alınır
 *   UPDATE: PUT  /api/admin/questions/{id} → aynı DTO güncelleme için kullanılır
 */
@Data
public class QuestionRequest {

    @NotBlank(message = "Soru metni boş olamaz")
    private String text;

    /** 4 şık — zorunlu */
    @NotNull
    @Size(min = 4, max = 4, message = "Tam olarak 4 seçenek girilmelidir")
    private List<String> options;

    /** 0-3 arası indeks */
    @NotNull(message = "Doğru cevap indeksi zorunludur")
    private Integer correctOptionIndex;

    @NotNull(message = "Zorluk seviyesi zorunludur")
    private Difficulty difficulty;

    @NotNull(message = "Kategori ID zorunludur")
    private Long categoryId;
}
