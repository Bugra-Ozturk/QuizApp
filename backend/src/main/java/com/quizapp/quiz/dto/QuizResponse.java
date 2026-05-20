package com.quizapp.quiz.dto;

import com.quizapp.question.dto.QuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Integer timeLimitSeconds;
    private Integer questionCount;
    private List<Long> questionIds;           // admin düzenleme için
    private List<QuestionResponse> questions; // null → liste görünümü; dolu → quiz oturumu
}
