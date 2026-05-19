package com.quizapp.quiz;

import com.quizapp.category.Category;
import com.quizapp.question.Question;
import com.quizapp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity'si.
 *
 * timeLimitSeconds : Frontend'deki geri sayım sayacına iletilir.
 * questions        : Bu quizde yer alan sorular (çoka-çok ilişki, sıralı).
 * createdBy        : Quizi oluşturan admin kullanıcısı.
 */
@Entity
@Table(name = "quizzes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    /** Quiz'in kategorisi */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Süre sınırı (saniye).
     * Örn: 300 → 5 dakika geri sayım.
     * Frontend bu değeri alır ve useCountdown composable'ına geçirir.
     */
    @Column(nullable = false)
    private Integer timeLimitSeconds;

    /**
     * Quiz'de yer alan sorular — ekleme sırası korunur (@OrderColumn).
     * Ara tablo: quiz_questions
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @OrderColumn(name = "question_order")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    /** Bu quizi oluşturan admin */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
