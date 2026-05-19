package com.quizapp.attempt;

import com.quizapp.attempt.dto.AttemptResponse;
import com.quizapp.attempt.dto.SubmitAttemptRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quiz çözme ve geçmiş sorgulama endpoint'leri.
 *
 * POST /api/attempts              → quiz cevaplarını gönder (kimlik doğrulamalı)
 * GET  /api/attempts/history      → kullanıcının geçmiş denemeleri
 * GET  /api/attempts/{id}         → tek deneme detayı
 * GET  /api/admin/attempts        → tüm denemeler (admin)
 */
@RestController
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    /** Quiz tamamlama — @AuthenticationPrincipal ile oturum açan kullanıcıyı al */
    @PostMapping("/api/attempts")
    public ResponseEntity<AttemptResponse> submit(
            @Valid @RequestBody SubmitAttemptRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(201)
                .body(attemptService.submit(request, userDetails.getUsername()));
    }

    /** Kullanıcının kendi geçmiş denemeleri */
    @GetMapping("/api/attempts/history")
    public ResponseEntity<List<AttemptResponse>> history(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attemptService.getHistory(userDetails.getUsername()));
    }

    /** Tek deneme detayı */
    @GetMapping("/api/attempts/{id}")
    public ResponseEntity<AttemptResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attemptService.getById(id, userDetails.getUsername()));
    }

    /** Admin: tüm kullanıcıların denemeleri */
    @GetMapping("/api/admin/attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttemptResponse>> getAllAdmin() {
        return ResponseEntity.ok(attemptService.getAllAdmin());
    }
}
