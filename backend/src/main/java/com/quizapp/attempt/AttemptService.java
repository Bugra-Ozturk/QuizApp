package com.quizapp.attempt;

import com.quizapp.attempt.dto.AttemptResponse;
import com.quizapp.attempt.dto.SubmitAttemptRequest;
import com.quizapp.common.exception.ApiException;
import com.quizapp.question.Question;
import com.quizapp.quiz.Quiz;
import com.quizapp.quiz.QuizRepository;
import com.quizapp.user.User;
import com.quizapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz çözme ve geçmiş sorgulama iş mantığı.
 *
 * ── Quiz Çözme Akışı ─────────────────────────────────────────────────────────
 * 1. Frontend, quiz ID + cevap listesi + süreyi POST /api/attempts ile gönderir
 * 2. Her cevap, ilgili sorunun correctOptionIndex'i ile karşılaştırılır
 * 3. Skor hesaplanır: (doğru / toplam) * 100
 * 4. Attempt ve AttemptAnswer'lar veritabanına kaydedilir
 * 5. Detaylı sonuç (doğru/yanlış dağılımı) frontend'e döner
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    /**
     * Quiz denemesini kaydet ve sonucu döndür.
     *
     * @Transactional: Attempt + AttemptAnswer'ların tek atomik işlemde kaydedilmesini sağlar.
     * Herhangi bir hata oluşursa tüm işlem geri alınır.
     */
    @Transactional
    public AttemptResponse submit(SubmitAttemptRequest request, String username) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ApiException("Quiz bulunamadı", HttpStatus.NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        List<Question> questions = quiz.getQuestions();
        List<Integer> userAnswers = request.getAnswers();

        if (userAnswers.size() != questions.size()) {
            throw new ApiException("Cevap sayısı soru sayısıyla eşleşmiyor", HttpStatus.BAD_REQUEST);
        }

        // Cevapları değerlendir
        int correctCount = 0;
        List<AttemptAnswer> answerEntities = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            int selected = userAnswers.get(i);
            boolean isCorrect = selected == question.getCorrectOptionIndex();
            if (isCorrect) correctCount++;

            answerEntities.add(AttemptAnswer.builder()
                    .question(question)
                    .selectedOptionIndex(selected)
                    .isCorrect(isCorrect)
                    .build());
        }

        // Skor hesapla
        double score = questions.isEmpty() ? 0 :
                (double) correctCount / questions.size() * 100;

        // Attempt oluştur
        Attempt attempt = Attempt.builder()
                .user(user)
                .quiz(quiz)
                .score(Math.round(score * 100.0) / 100.0)
                .totalQuestions(questions.size())
                .correctCount(correctCount)
                .durationSeconds(request.getDurationSeconds())
                .answers(answerEntities)
                .build();

        // Çift yönlü ilişki kur (cascade ile cevaplar da kaydedilir)
        answerEntities.forEach(a -> a.setAttempt(attempt));

        return toResponse(attemptRepository.save(attempt));
    }

    /** Kullanıcının kendi geçmiş denemeleri */
    public List<AttemptResponse> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        return attemptRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Tek deneme detayı */
    public AttemptResponse getById(Long id, String username) {
        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() -> new ApiException("Deneme bulunamadı", HttpStatus.NOT_FOUND));

        // Kullanıcı yalnızca kendi denemesini görebilir
        if (!attempt.getUser().getUsername().equals(username)) {
            throw new ApiException("Bu denemeye erişim yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        return toResponse(attempt);
    }

    /** Admin: tüm denemeler */
    public List<AttemptResponse> getAllAdmin() {
        return attemptRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private AttemptResponse toResponse(Attempt attempt) {
        List<AttemptResponse.AnswerDetail> details = attempt.getAnswers().stream()
                .map(a -> AttemptResponse.AnswerDetail.builder()
                        .questionId(a.getQuestion().getId())
                        .questionText(a.getQuestion().getText())
                        .options(a.getQuestion().getOptions())
                        .selectedOptionIndex(a.getSelectedOptionIndex())
                        .correctOptionIndex(a.getQuestion().getCorrectOptionIndex())
                        .isCorrect(a.getIsCorrect())
                        .build())
                .toList();

        return AttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .username(attempt.getUser().getUsername())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .correctCount(attempt.getCorrectCount())
                .wrongCount(attempt.getTotalQuestions() - attempt.getCorrectCount())
                .durationSeconds(attempt.getDurationSeconds())
                .createdAt(attempt.getCreatedAt())
                .answers(details)
                .build();
    }
}
