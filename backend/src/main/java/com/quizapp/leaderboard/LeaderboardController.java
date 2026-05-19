package com.quizapp.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Liderlik tablosu endpoint'leri.
 *
 * GET /api/leaderboard           → genel liderlik (ortalama skor)
 * GET /api/leaderboard?quizId=1  → quiz bazlı liderlik (en yüksek skor)
 */
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam(required = false) Long quizId) {
        if (quizId != null) {
            return ResponseEntity.ok(leaderboardService.getQuizLeaderboard(quizId));
        }
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }
}
