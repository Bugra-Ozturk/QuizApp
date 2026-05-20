package com.quizapp.quiz;

import com.quizapp.attempt.AttemptRepository;
import com.quizapp.category.Category;
import com.quizapp.category.CategoryRepository;
import com.quizapp.common.exception.ApiException;
import com.quizapp.question.Question;
import com.quizapp.question.QuestionRepository;
import com.quizapp.question.QuestionService;
import com.quizapp.quiz.dto.QuizRequest;
import com.quizapp.quiz.dto.QuizResponse;
import com.quizapp.user.User;
import com.quizapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final UserRepository userRepository;
    private final AttemptRepository attemptRepository;

    public List<QuizResponse> findAll() {
        return quizRepository.findAll().stream()
                .map(q -> toSummaryResponse(q))
                .toList();
    }

    public List<QuizResponse> findByCategory(Long categoryId) {
        return quizRepository.findByCategoryId(categoryId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    /**
     * Quiz oturumu için soruları döndür — doğru cevaplar gizlenir.
     * timeLimitSeconds frontend countdown'una iletilir.
     */
    public QuizResponse findForPlay(Long id) {
        Quiz quiz = getOrThrow(id);
        List<com.quizapp.question.dto.QuestionResponse> questions = quiz.getQuestions().stream()
                .map(questionService::toPublicResponse)
                .toList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .timeLimitSeconds(quiz.getTimeLimitSeconds())
                .questionCount(questions.size())
                .questions(questions)
                .build();
    }

    /** Admin düzenleme formu için quiz detayı — questionIds dahil */
    public QuizResponse findForAdmin(Long id) {
        Quiz quiz = getOrThrow(id);
        List<Long> questionIds = quiz.getQuestions().stream()
                .map(q -> q.getId())
                .toList();
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .timeLimitSeconds(quiz.getTimeLimitSeconds())
                .questionCount(questionIds.size())
                .questionIds(questionIds)
                .build();
    }

    public QuizResponse create(QuizRequest request, String creatorUsername) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Kategori bulunamadı", HttpStatus.NOT_FOUND));

        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .category(category)
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .questions(questions)
                .createdBy(creator)
                .build();

        return toSummaryResponse(quizRepository.save(quiz));
    }

    public QuizResponse update(Long id, QuizRequest request) {
        Quiz quiz = getOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Kategori bulunamadı", HttpStatus.NOT_FOUND));
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());

        quiz.setTitle(request.getTitle());
        quiz.setCategory(category);
        quiz.setTimeLimitSeconds(request.getTimeLimitSeconds());
        quiz.setQuestions(questions);

        return toSummaryResponse(quizRepository.save(quiz));
    }

    @Transactional
    public void delete(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ApiException("Quiz bulunamadı: " + id, HttpStatus.NOT_FOUND);
        }
        // Önce bağlı denemeleri sil (FK constraint: attempts.quiz_id → quizzes.id)
        attemptRepository.deleteByQuizId(id);
        quizRepository.deleteById(id);
    }

    private Quiz getOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ApiException("Quiz bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    /** Sorular olmadan özet yanıt (liste görünümü için) */
    private QuizResponse toSummaryResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .timeLimitSeconds(quiz.getTimeLimitSeconds())
                .questionCount(quiz.getQuestions().size())
                .build();
    }
}
