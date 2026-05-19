package com.quizapp.attempt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Quiz tamamlama isteği.
 * Frontend, kullanıcının verdiği tüm cevapları ve geçen süreyi gönderir.
 */
@Data
public class SubmitAttemptRequest {

    @NotNull
    private Long quizId;

    /** Her soruya verilen cevap indeksi (-1: cevaplanmadı) */
    @NotNull
    private List<Integer> answers;

    /** Kullanıcının harcadığı toplam süre (saniye) */
    @NotNull
    private Integer durationSeconds;
}
