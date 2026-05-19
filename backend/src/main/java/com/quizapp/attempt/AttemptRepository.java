package com.quizapp.attempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    /** Kullanıcının geçmiş denemeleri — en yeni önce */
    List<Attempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Admin: belirli bir quiz'in tüm denemeleri */
    List<Attempt> findByQuizIdOrderByCreatedAtDesc(Long quizId);

    /**
     * Leaderboard sorgusu: her kullanıcı için en yüksek skoru döndür.
     * JPQL ile yazılmıştır — veri tabanından bağımsız çalışır.
     */
    @Query("""
        SELECT a FROM Attempt a
        WHERE a.score = (
            SELECT MAX(a2.score) FROM Attempt a2
            WHERE a2.user.id = a.user.id
        )
        ORDER BY a.score DESC, a.createdAt ASC
        """)
    List<Attempt> findTopScorePerUser();

    /** Admin paneli için tüm denemeler */
    List<Attempt> findAllByOrderByCreatedAtDesc();
}
