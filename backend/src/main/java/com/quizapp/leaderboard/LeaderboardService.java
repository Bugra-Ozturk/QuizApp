package com.quizapp.leaderboard;

import com.quizapp.attempt.AttemptRepository;
import com.quizapp.quiz.Quiz;
import com.quizapp.quiz.QuizRepository;
import com.quizapp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.quizapp.common.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Liderlik tablosu servisi.
 *
 * Genel mod : tüm quizlerdeki deneme ortalamasına göre sıralama.
 * Quiz modu : tek bir quizdeki en yüksek skora göre sıralama.
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final AttemptRepository attemptRepository;
    private final QuizRepository    quizRepository;

    /**
     * Genel liderlik — tüm kullanıcıların genel başarı ortalaması.
     * Kullanıcı kaç farklı quiz çözmüş olursa olsun, tüm denemelerinin
     * ortalama skoru hesaplanır.
     */
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        List<Object[]> rows = attemptRepository.findGlobalLeaderboard();
        List<LeaderboardEntry> result = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            User   user     = (User)   row[0];
            Double avgScore = (Double) row[1];
            Long   count    = (Long)   row[2];

            // En yüksek skorlu denemeyi ayrı sorgu ile çekiyoruz
            // (PostgreSQL GROUP BY içinde correlated subquery desteklenmiyor)
            List<com.quizapp.attempt.Attempt> topAttempts =
                    attemptRepository.findTopAttemptByUser(user.getId());
            String bestQuiz = topAttempts.isEmpty()
                    ? "-"
                    : topAttempts.get(0).getQuiz().getTitle();

            result.add(new LeaderboardEntry(
                    i + 1,
                    user.getUsername(),
                    Math.round(avgScore * 100.0) / 100.0,
                    count.intValue(),
                    bestQuiz
            ));
        }
        return result;
    }

    /**
     * Quiz bazlı liderlik — belirli bir quiz'deki en yüksek skora göre sıralama.
     * Aynı kullanıcı aynı quizi birden fazla çözdüyse en iyi sonucu alınır.
     */
    public List<LeaderboardEntry> getQuizLeaderboard(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ApiException("Quiz bulunamadı", HttpStatus.NOT_FOUND));

        List<Object[]> rows = attemptRepository.findQuizLeaderboard(quizId);
        List<LeaderboardEntry> result = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            User   user     = (User)   row[0];
            Double maxScore = (Double) row[1];
            Long   count    = (Long)   row[2];

            result.add(new LeaderboardEntry(
                    i + 1,
                    user.getUsername(),
                    Math.round(maxScore * 100.0) / 100.0,
                    count.intValue(),
                    quiz.getTitle()
            ));
        }
        return result;
    }
}
