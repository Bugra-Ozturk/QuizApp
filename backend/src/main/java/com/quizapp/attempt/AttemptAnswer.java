package com.quizapp.attempt;

import com.quizapp.question.Question;
import jakarta.persistence.*;
import lombok.*;

/**
 * Bir deneme içindeki tek bir soruya verilen cevabı tutar.
 */
@Entity
@Table(name = "attempt_answers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** Kullanıcının seçtiği şıkkın 0-tabanlı indeksi (-1: cevaplanmadı/süre doldu) */
    @Column(nullable = false)
    private Integer selectedOptionIndex;

    /** Cevap doğru mu? */
    @Column(nullable = false)
    private Boolean isCorrect;
}
