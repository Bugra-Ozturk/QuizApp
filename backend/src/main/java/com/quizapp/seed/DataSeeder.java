package com.quizapp.seed;

import com.quizapp.category.Category;
import com.quizapp.category.CategoryRepository;
import com.quizapp.question.Difficulty;
import com.quizapp.question.Question;
import com.quizapp.question.QuestionRepository;
import com.quizapp.quiz.Quiz;
import com.quizapp.quiz.QuizRepository;
import com.quizapp.user.Role;
import com.quizapp.user.User;
import com.quizapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Uygulama başlarken çalışan başlangıç veri yükleyicisi.
 *
 * CommandLineRunner: Spring Boot uygulaması ayağa kalktıktan sonra run() metodunu çağırır.
 * Veritabanı zaten doluysa seed işlemi atlanır (idempotent).
 *
 * Hazır hesaplar:
 *   Admin: username=admin,  password=admin123
 *   User:  username=user,   password=user123
 *   User:  username=ahmet,  password=ahmet123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Veritabanı doluysa seed'i atla
        if (userRepository.count() > 0) {
            log.info("Veritabanı zaten dolu, seed atlandı.");
            return;
        }

        log.info("Başlangıç verileri yükleniyor...");

        // ── Kullanıcılar ─────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@quizapp.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        User user1 = userRepository.save(User.builder()
                .username("user")
                .email("user@quizapp.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .build());

        userRepository.save(User.builder()
                .username("ahmet")
                .email("ahmet@quizapp.com")
                .password(passwordEncoder.encode("ahmet123"))
                .role(Role.USER)
                .build());

        // ── Kategoriler ───────────────────────────────────────────────────────
        Category matematik = categoryRepository.save(
                Category.builder().name("Matematik").description("Sayılar, cebir ve geometri").build());

        Category tarih = categoryRepository.save(
                Category.builder().name("Tarih").description("Dünya ve Türkiye tarihi").build());

        Category bilim = categoryRepository.save(
                Category.builder().name("Bilim").description("Fizik, kimya ve biyoloji").build());

        // ── Sorular: Matematik ────────────────────────────────────────────────
        Question m1 = questionRepository.save(Question.builder()
                .text("2 + 2 x 2 işleminin sonucu nedir?")
                .options(List.of("8", "6", "4", "12"))
                .correctOptionIndex(1)      // Cevap: 6 (işlem önceliği)
                .difficulty(Difficulty.EASY)
                .category(matematik)
                .build());

        Question m2 = questionRepository.save(Question.builder()
                .text("Bir karenin alanı 81 cm² ise kenar uzunluğu kaçtır?")
                .options(List.of("7 cm", "8 cm", "9 cm", "10 cm"))
                .correctOptionIndex(2)      // Cevap: 9 cm
                .difficulty(Difficulty.EASY)
                .category(matematik)
                .build());

        Question m3 = questionRepository.save(Question.builder()
                .text("x² - 5x + 6 = 0 denkleminin kökleri nelerdir?")
                .options(List.of("2 ve 3", "1 ve 6", "-2 ve -3", "2 ve -3"))
                .correctOptionIndex(0)      // Cevap: 2 ve 3
                .difficulty(Difficulty.MEDIUM)
                .category(matematik)
                .build());

        Question m4 = questionRepository.save(Question.builder()
                .text("log₂(64) değeri nedir?")
                .options(List.of("5", "6", "7", "8"))
                .correctOptionIndex(1)      // Cevap: 6
                .difficulty(Difficulty.MEDIUM)
                .category(matematik)
                .build());

        Question m5 = questionRepository.save(Question.builder()
                .text("∫(2x)dx ifadesinin belirsiz integrali nedir?")
                .options(List.of("x + C", "x² + C", "2 + C", "2x² + C"))
                .correctOptionIndex(1)      // Cevap: x² + C
                .difficulty(Difficulty.HARD)
                .category(matematik)
                .build());

        // ── Sorular: Tarih ────────────────────────────────────────────────────
        Question t1 = questionRepository.save(Question.builder()
                .text("Türkiye Cumhuriyeti hangi yılda kurulmuştur?")
                .options(List.of("1919", "1920", "1923", "1922"))
                .correctOptionIndex(2)      // Cevap: 1923
                .difficulty(Difficulty.EASY)
                .category(tarih)
                .build());

        Question t2 = questionRepository.save(Question.builder()
                .text("İstanbul'un fethi hangi yılda gerçekleşmiştir?")
                .options(List.of("1453", "1492", "1389", "1402"))
                .correctOptionIndex(0)      // Cevap: 1453
                .difficulty(Difficulty.EASY)
                .category(tarih)
                .build());

        Question t3 = questionRepository.save(Question.builder()
                .text("Kurtuluş Savaşı'nın başlangıcı hangi olayla kabul edilir?")
                .options(List.of("Çanakkale Savaşı", "Samsun'a çıkış", "TBMM'nin açılışı", "Lozan Antlaşması"))
                .correctOptionIndex(1)      // Cevap: Samsun'a çıkış
                .difficulty(Difficulty.MEDIUM)
                .category(tarih)
                .build());

        Question t4 = questionRepository.save(Question.builder()
                .text("Osmanlı İmparatorluğu'nun son padişahı kimdir?")
                .options(List.of("Abdülhamit II", "Mehmet VI Vahdettin", "Abdülmecit II", "Mehmet V"))
                .correctOptionIndex(1)      // Cevap: Mehmet VI Vahdettin
                .difficulty(Difficulty.MEDIUM)
                .category(tarih)
                .build());

        Question t5 = questionRepository.save(Question.builder()
                .text("Lozan Antlaşması hangi tarihte imzalanmıştır?")
                .options(List.of("1920", "1921", "1922", "1923"))
                .correctOptionIndex(3)      // Cevap: 1923
                .difficulty(Difficulty.HARD)
                .category(tarih)
                .build());

        // ── Sorular: Bilim ────────────────────────────────────────────────────
        Question b1 = questionRepository.save(Question.builder()
                .text("Işığın boşluktaki hızı yaklaşık kaç km/s'dir?")
                .options(List.of("150.000 km/s", "200.000 km/s", "300.000 km/s", "400.000 km/s"))
                .correctOptionIndex(2)      // Cevap: 300.000 km/s
                .difficulty(Difficulty.EASY)
                .category(bilim)
                .build());

        Question b2 = questionRepository.save(Question.builder()
                .text("Su molekülünün kimyasal formülü nedir?")
                .options(List.of("H₂O₂", "H₂O", "HO₂", "H₃O"))
                .correctOptionIndex(1)      // Cevap: H₂O
                .difficulty(Difficulty.EASY)
                .category(bilim)
                .build());

        Question b3 = questionRepository.save(Question.builder()
                .text("DNA'nın çift sarmal yapısını kim keşfetmiştir?")
                .options(List.of("Einstein ve Bohr", "Watson ve Crick", "Mendel ve Darwin", "Pasteur ve Koch"))
                .correctOptionIndex(1)      // Cevap: Watson ve Crick
                .difficulty(Difficulty.MEDIUM)
                .category(bilim)
                .build());

        Question b4 = questionRepository.save(Question.builder()
                .text("Periyodik tabloda 'Au' sembolü hangi elementi temsil eder?")
                .options(List.of("Gümüş", "Alüminyum", "Altın", "Argon"))
                .correctOptionIndex(2)      // Cevap: Altın
                .difficulty(Difficulty.MEDIUM)
                .category(bilim)
                .build());

        Question b5 = questionRepository.save(Question.builder()
                .text("Schrödinger denklemi hangi alanda kullanılır?")
                .options(List.of("Termodinamik", "Kuantum mekaniği", "Genel görelilik", "Elektromanyetizma"))
                .correctOptionIndex(1)      // Cevap: Kuantum mekaniği
                .difficulty(Difficulty.HARD)
                .category(bilim)
                .build());

        // ── Quizler ───────────────────────────────────────────────────────────
        quizRepository.save(Quiz.builder()
                .title("Matematik Temelleri")
                .category(matematik)
                .timeLimitSeconds(300)      // 5 dakika
                .questions(List.of(m1, m2, m3, m4, m5))
                .createdBy(admin)
                .build());

        quizRepository.save(Quiz.builder()
                .title("Türk Tarihi")
                .category(tarih)
                .timeLimitSeconds(240)      // 4 dakika
                .questions(List.of(t1, t2, t3, t4, t5))
                .createdBy(admin)
                .build());

        quizRepository.save(Quiz.builder()
                .title("Bilim Dünyası")
                .category(bilim)
                .timeLimitSeconds(300)      // 5 dakika
                .questions(List.of(b1, b2, b3, b4, b5))
                .createdBy(admin)
                .build());

        quizRepository.save(Quiz.builder()
                .title("Hızlı Matematik")
                .category(matematik)
                .timeLimitSeconds(120)      // 2 dakika — hızlı test
                .questions(List.of(m1, m2, m3))
                .createdBy(admin)
                .build());

        log.info("Seed tamamlandı: 3 kullanıcı, 3 kategori, 15 soru, 4 quiz yüklendi.");
    }
}
