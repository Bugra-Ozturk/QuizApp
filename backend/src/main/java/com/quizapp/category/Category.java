package com.quizapp.category;

import jakarta.persistence.*;
import lombok.*;

/**
 * Soru ve quiz kategorisi (ör: Matematik, Tarih, Bilim).
 */
@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 255)
    private String description;
}
