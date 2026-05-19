package com.quizapp.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/** Quiz oluşturma/güncelleme isteği */
@Data
public class QuizRequest {

    @NotBlank(message = "Quiz başlığı boş olamaz")
    private String title;

    @NotNull(message = "Kategori ID zorunludur")
    private Long categoryId;

    @NotNull(message = "Süre sınırı zorunludur")
    @Positive(message = "Süre pozitif olmalıdır")
    private Integer timeLimitSeconds;

    /** Quiz'e eklenecek soru ID listesi */
    @NotNull
    private List<Long> questionIds;
}
