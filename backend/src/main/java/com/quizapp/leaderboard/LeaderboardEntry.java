package com.quizapp.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Liderlik tablosu satırı — entity değil, hafızada oluşturulur */
@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private Integer rank;
    private String username;
    private Double bestScore;
    private Integer totalAttempts;
    private String quizTitle;
}
