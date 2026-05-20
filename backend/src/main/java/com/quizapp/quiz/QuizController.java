package com.quizapp.quiz;

import com.quizapp.quiz.dto.QuizRequest;
import com.quizapp.quiz.dto.QuizResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quiz endpoint'leri.
 *
 * GET  /api/quizzes              → herkese açık (liste)
 * GET  /api/quizzes/{id}/play    → kimliği doğrulanmış kullanıcı (sorularla)
 * POST /api/admin/quizzes        → yalnızca ADMIN
 * PUT  /api/admin/quizzes/{id}   → yalnızca ADMIN
 * DELETE /api/admin/quizzes/{id} → yalnızca ADMIN
 */
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/api/quizzes")
    public ResponseEntity<List<QuizResponse>> getAll(
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(quizService.findByCategory(categoryId));
        }
        return ResponseEntity.ok(quizService.findAll());
    }

    /** Quiz'i sorularıyla birlikte döndür (doğru cevaplar gizli) */
    @GetMapping("/api/quizzes/{id}/play")
    public ResponseEntity<QuizResponse> play(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.findForPlay(id));
    }

    /** Admin düzenleme formu: quiz detayı + questionIds */
    @GetMapping("/api/admin/quizzes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> getForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.findForAdmin(id));
    }

    @PostMapping("/api/admin/quizzes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> create(
            @Valid @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(201).body(quizService.create(request, userDetails.getUsername()));
    }

    @PutMapping("/api/admin/quizzes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.update(id, request));
    }

    @DeleteMapping("/api/admin/quizzes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
