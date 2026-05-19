package com.quizapp.attempt;

import com.quizapp.quiz.Quiz;
import com.quizapp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Kullanıcının bir quiz denemesini temsil eder.
 *
 * score          : Yüzde cinsinden puan (0-100)
 * correctCount   : Doğru cevap sayısı
 * durationSeconds: Kullanıcının quizi tamamlamak için harcadığı süre
 * answers        : Her soru için verilen cevaplar (cascade ile birlikte kaydedilir)
 */
@Entity
@Table(name = "attempts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    /** Puan: (doğru / toplam) * 100 */
    @Column(nullable = false)
    private Double score;

    /** Toplam soru sayısı */
    @Column(nullable = false)
    private Integer totalQuestions;

    /** Doğru cevaplanan soru sayısı */
    @Column(nullable = false)
    private Integer correctCount;

    /** Quizi tamamlamak için geçen süre (saniye) */
    @Column(nullable = false)
    private Integer durationSeconds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Bu denemeye ait tüm cevaplar.
     * CascadeType.ALL: attempt kaydedilince/silinince cevaplar da kaydedilir/silinir.
     */
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttemptAnswer> answers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
