package com.quizapp.question;

import com.quizapp.question.dto.QuestionRequest;
import com.quizapp.question.dto.QuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Soru CRUD REST endpoint'leri — yalnızca ADMIN erişimi.
 *
 * GET    /api/admin/questions            → tüm sorular
 * GET    /api/admin/questions/{id}       → tek soru
 * GET    /api/admin/questions?categoryId → kategori filtreli
 * POST   /api/admin/questions            → yeni soru
 * PUT    /api/admin/questions/{id}       → güncelle
 * DELETE /api/admin/questions/{id}       → sil
 */
@RestController
@RequestMapping("/api/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAll(
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(questionService.findByCategory(categoryId));
        }
        return ResponseEntity.ok(questionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.status(201).body(questionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
