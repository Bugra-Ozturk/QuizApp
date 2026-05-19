package com.quizapp.attempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    /** Quiz silinirken bağlı tüm denemeleri kaldırır */
    @Transactional
    void deleteByQuizId(Long quizId);

    /** Kullanıcının geçmiş denemeleri — en yeni önce */
    List<Attempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Admin: belirli bir quiz'in tüm denemeleri */
    List<Attempt> findByQuizIdOrderByCreatedAtDesc(Long quizId);

    /** Admin paneli için tüm denemeler */
    List<Attempt> findAllByOrderByCreatedAtDesc();

    /**
     * Genel liderlik: kullanıcı başına ortalama skor + deneme sayısı.
     * bestQuizTitle ayrıca servis katmanında çekilir (PostgreSQL correlated
     * subquery kısıtı nedeniyle burada sorgulanmaz).
     * Object[]: [User, avgScore, attemptCount]
     */
    @Query("""
        SELECT a.user, AVG(a.score), COUNT(a)
        FROM Attempt a
        GROUP BY a.user
        ORDER BY AVG(a.score) DESC
        """)
    List<Object[]> findGlobalLeaderboard();

    /**
     * Kullanıcının en yüksek skorlu denemesini getirir (best quiz title için).
     */
    @Query("""
        SELECT a FROM Attempt a
        WHERE a.user.id = :userId
        ORDER BY a.score DESC, a.createdAt ASC
        """)
    List<Attempt> findTopAttemptByUser(@Param("userId") Long userId);

    /**
     * Quiz bazlı liderlik: belirli bir quiz'de her kullanıcının en yüksek skoru.
     * Object[]: [User, maxScore, attemptCount]
     */
    @Query("""
        SELECT a.user, MAX(a.score), COUNT(a)
        FROM Attempt a
        WHERE a.quiz.id = :quizId
        GROUP BY a.user
        ORDER BY MAX(a.score) DESC
        """)
    List<Object[]> findQuizLeaderboard(@Param("quizId") Long quizId);
}
