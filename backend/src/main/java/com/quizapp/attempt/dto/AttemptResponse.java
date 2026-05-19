package com.quizapp.attempt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** Quiz denemesi sonuç DTO — frontend bu verilerle ECharts grafiğini oluşturur */
@Data
@Builder
public class AttemptResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Double score;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private List<AnswerDetail> answers;

    @Data
    @Builder
    public static class AnswerDetail {
        private Long questionId;
        private String questionText;
        private List<String> options;
        private Integer selectedOptionIndex;
        private Integer correctOptionIndex;
        private Boolean isCorrect;
    }
}
