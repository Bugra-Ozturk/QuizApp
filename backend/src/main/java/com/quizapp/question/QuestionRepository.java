package com.quizapp.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Belirli bir kategoriye ait soruları listele */
    List<Question> findByCategoryId(Long categoryId);

    /** Quiz oluşturma sırasında kategori + zorluk filtrelemesi */
    List<Question> findByCategoryIdAndDifficulty(Long categoryId, Difficulty difficulty);
}
