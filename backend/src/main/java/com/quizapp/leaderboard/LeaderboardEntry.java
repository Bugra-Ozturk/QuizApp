package com.quizapp.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Liderlik tablosu satırı — entity değil, hafızada oluşturulur.
 *
 * Genel mod  : avgScore = kullanıcının tüm denemelerinin ortalaması
 * Quiz modu  : avgScore = o quizdeki en yüksek skor
 */
@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private Integer rank;
    private String  username;
    private Double  avgScore;       // genel: ortalama, quiz: en yüksek
    private Integer totalAttempts;  // toplam deneme sayısı
    private String  bestQuizTitle;  // en yüksek skorun alındığı quiz adı
}
