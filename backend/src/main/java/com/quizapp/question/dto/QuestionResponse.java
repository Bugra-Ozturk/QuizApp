package com.quizapp.question.dto;

import com.quizapp.question.Difficulty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Soru yanıt DTO.
 *
 * NOT: correctOptionIndex yalnızca admin endpoint'lerinde döner.
 * Kullanıcıya quiz soruları gönderilirken bu alan null bırakılır —
 * böylece doğru cevap client-side açık olmaz.
 */
@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String text;
    private List<String> options;
    private Integer correctOptionIndex; // null → quiz modu, dolu → admin modu
    private Difficulty difficulty;
    private Long categoryId;
    private String categoryName;
}
