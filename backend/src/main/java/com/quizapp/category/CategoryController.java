package com.quizapp.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kategori REST endpoint'leri.
 *
 * GET  /api/categories         → herkese açık
 * GET  /api/categories/{id}    → herkese açık
 * POST /api/admin/categories   → yalnızca ADMIN
 * PUT  /api/admin/categories/{id}  → yalnızca ADMIN
 * DELETE /api/admin/categories/{id} → yalnızca ADMIN
 */
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping("/api/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.status(201).body(categoryService.create(category));
    }

    @PutMapping("/api/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
