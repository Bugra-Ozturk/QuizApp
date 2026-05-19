package com.quizapp.question;

import com.quizapp.category.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Quiz sorusu entity'si.
 *
 * options   : 4 seçenek metni (PostgreSQL'de text[] olarak saklanır)
 * correctOptionIndex: Doğru seçeneğin 0 tabanlı indeksi (0-3)
 * difficulty: EASY | MEDIUM | HARD — leaderboard ağırlıklandırması için kullanılabilir
 */
@Entity
@Table(name = "questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Soru metni */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    /**
     * Dört şıktan oluşan liste.
     * @ElementCollection ile ayrı bir tabloda (question_options) saklanır.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", nullable = false)
    @OrderColumn(name = "option_order")
    private List<String> options;

    /** Doğru şıkkın 0-tabanlı indeksi */
    @Column(nullable = false)
    private Integer correctOptionIndex;

    /** Sorunun zorluk seviyesi */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    /** Bu sorunun ait olduğu kategori */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
