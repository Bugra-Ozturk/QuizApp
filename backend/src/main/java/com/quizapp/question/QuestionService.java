package com.quizapp.question;

import com.quizapp.category.Category;
import com.quizapp.category.CategoryRepository;
import com.quizapp.common.exception.ApiException;
import com.quizapp.question.dto.QuestionRequest;
import com.quizapp.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Soru CRUD iş mantığı.
 *
 * ── CRUD Açıklaması (Ödev Dokümantasyonu) ────────────────────────────────────
 *
 * CREATE (Oluşturma):
 *   - Admin yeni soru verisini QuestionRequest DTO olarak gönderir
 *   - Kategori varlığı kontrol edilir (yoksa 404)
 *   - Question entity oluşturulur, veritabanına kaydedilir
 *   - 201 Created + soru bilgisi döner
 *
 * READ (Okuma):
 *   - Tüm soruları listele: GET /api/admin/questions
 *   - ID ile getir: GET /api/admin/questions/{id}
 *   - Kategori filtreli: GET /api/admin/questions?categoryId=1
 *
 * UPDATE (Güncelleme):
 *   - Mevcut sorunun tüm alanları yeni değerlerle değiştirilir (tam güncelleme)
 *   - PUT /api/admin/questions/{id}
 *
 * DELETE (Silme):
 *   - Soru veritabanından kalıcı olarak silinir
 *   - DELETE /api/admin/questions/{id}
 *   - 204 No Content döner
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    public List<QuestionResponse> findAll() {
        return questionRepository.findAll().stream()
                .map(q -> toResponse(q, true))
                .toList();
    }

    public List<QuestionResponse> findByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId).stream()
                .map(q -> toResponse(q, true))
                .toList();
    }

    public QuestionResponse findById(Long id) {
        return toResponse(getOrThrow(id), true);
    }

    /** Soru oluşturma — yalnızca admin */
    public QuestionResponse create(QuestionRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Kategori bulunamadı", HttpStatus.NOT_FOUND));

        Question question = Question.builder()
                .text(request.getText())
                .options(request.getOptions())
                .correctOptionIndex(request.getCorrectOptionIndex())
                .difficulty(request.getDifficulty())
                .category(category)
                .build();

        return toResponse(questionRepository.save(question), true);
    }

    /** Soru güncelleme — yalnızca admin */
    public QuestionResponse update(Long id, QuestionRequest request) {
        Question question = getOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Kategori bulunamadı", HttpStatus.NOT_FOUND));

        question.setText(request.getText());
        question.setOptions(request.getOptions());
        question.setCorrectOptionIndex(request.getCorrectOptionIndex());
        question.setDifficulty(request.getDifficulty());
        question.setCategory(category);

        return toResponse(questionRepository.save(question), true);
    }

    /** Soru silme — yalnızca admin */
    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ApiException("Soru bulunamadı: " + id, HttpStatus.NOT_FOUND);
        }
        questionRepository.deleteById(id);
    }

    /**
     * Quiz oturumu için soruyu döndürür — doğru cevap gizlenir.
     * Bu sayede frontend'de cevap açık olmaz.
     */
    public QuestionResponse toPublicResponse(Question question) {
        return toResponse(question, false);
    }

    private Question getOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Soru bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    /** includeAnswer=true → admin; false → quiz modunda kullanıcıya gönderim */
    private QuestionResponse toResponse(Question q, boolean includeAnswer) {
        return QuestionResponse.builder()
                .id(q.getId())
                .text(q.getText())
                .options(q.getOptions())
                .correctOptionIndex(includeAnswer ? q.getCorrectOptionIndex() : null)
                .difficulty(q.getDifficulty())
                .categoryId(q.getCategory().getId())
                .categoryName(q.getCategory().getName())
                .build();
    }
}
