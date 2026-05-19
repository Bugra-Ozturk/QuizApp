package com.quizapp.leaderboard;

import com.quizapp.attempt.Attempt;
import com.quizapp.attempt.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Liderlik tablosu: her kullanıcının en yüksek skoru sıralanır.
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final AttemptRepository attemptRepository;

    public List<LeaderboardEntry> getLeaderboard() {
        // Her kullanıcının en yüksek skorlu denemesini getir (repository sorgusu)
        List<Attempt> topAttempts = attemptRepository.findTopScorePerUser();

        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        for (int i = 0; i < topAttempts.size(); i++) {
            Attempt a = topAttempts.get(i);

            // Kullanıcının toplam deneme sayısını hesapla
            long totalAttempts = attemptRepository
                    .findByUserIdOrderByCreatedAtDesc(a.getUser().getId()).size();

            leaderboard.add(new LeaderboardEntry(
                    i + 1,                          // sıra (1-tabanlı)
                    a.getUser().getUsername(),
                    a.getScore(),
                    (int) totalAttempts,
                    a.getQuiz().getTitle()
            ));
        }
        return leaderboard;
    }
}
