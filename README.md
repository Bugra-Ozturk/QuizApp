# QuizApp

Online quiz uygulaması — Spring Boot 3 + Vue 3 + PostgreSQL + Docker.

---

## İçindekiler

1. [Teknoloji Yığını](#teknoloji-yığını)
2. [Proje Yapısı](#proje-yapısı)
3. [Hızlı Başlangıç (Docker Compose)](#hızlı-başlangıç)
4. [Geliştirme Ortamı](#geliştirme-ortamı)
5. [Kimlik Doğrulama (Authentication)](#kimlik-doğrulama)
6. [Yetkilendirme (Authorization)](#yetkilendirme)
7. [CRUD İşlemleri](#crud-işlemleri)
8. [API Referansı](#api-referansı)
9. [Hazır Kullanıcılar (Seed)](#hazır-kullanıcılar)
10. [Geliştirilebilir Özellikler](#geliştirilebilir-özellikler)

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Backend | Spring Boot 3.2, Java 21, Spring Security, Spring Data JPA |
| Veritabanı | PostgreSQL 16 |
| Auth | JWT (jjwt 0.12), BCryptPasswordEncoder |
| Frontend | Vue 3 (Composition API), Vite, Tailwind CSS |
| State | Pinia |
| HTTP | Axios (interceptor ile JWT) |
| Grafik | Apache ECharts (vue-echarts) |
| Bildirim | vue-toastification |
| İkon | Font Awesome |
| Sunucu | Caddy 2 (SPA + reverse proxy) |
| Altyapı | Docker, Docker Compose |

---

## Proje Yapısı

```
QuizApp/
├── docker-compose.yml
├── .gitignore
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/quizapp/
│       ├── common/          # exception, CORS
│       ├── security/        # JWT filtresi, SecurityConfig
│       ├── auth/            # kayıt/giriş
│       ├── user/            # User entity + repo
│       ├── category/        # kategori CRUD
│       ├── question/        # soru CRUD
│       ├── quiz/            # quiz CRUD
│       ├── attempt/         # quiz çözme, geçmiş
│       ├── leaderboard/     # liderlik tablosu
│       └── seed/            # başlangıç verisi
└── frontend/
    ├── Dockerfile
    ├── Caddyfile
    └── src/
        ├── api/             # axios instance
        ├── composables/     # useCountdown
        ├── stores/          # Pinia auth store
        ├── router/          # Vue Router + guard
        ├── components/      # Navbar
        └── features/
            ├── auth/
            ├── quiz/
            ├── results/
            ├── leaderboard/
            └── admin/
```

---

## Hızlı Başlangıç

### Gereksinimler

- Docker ≥ 24
- Docker Compose ≥ 2.20

### Başlatma

```bash
# 1. Depoyu klonlayın
git clone <repo-url> QuizApp
cd QuizApp

# 2. Tüm servisleri derleyip başlatın
docker compose up --build

# 3. Tarayıcıda açın
# Frontend: http://localhost
# Backend:  http://localhost:8080/api
```

Servisler:

| Servis | Port | Açıklama |
|---|---|---|
| frontend | 80 | Vue SPA (Caddy) |
| backend | 8080 | Spring Boot REST API |
| db | 5432 | PostgreSQL |

```bash
# Durdurmak için
docker compose down

# Veritabanıyla birlikte temizlemek için
docker compose down -v
```

---

## Geliştirme Ortamı

### Backend (yerel)

```bash
cd backend

# PostgreSQL çalışıyor olmalı (docker compose up db yeterli)
docker compose up db -d

# Spring Boot başlat
./mvnw spring-boot:run
# → http://localhost:8080
```

### Frontend (yerel)

```bash
cd frontend

npm install
npm run dev
# → http://localhost:5173

# Vite, /api/* isteklerini otomatik olarak localhost:8080'e proxy'ler
```

---

## Kimlik Doğrulama

> **Ödev Açıklaması — Authentication (Kimlik Doğrulama)**

### Nedir?

Kimlik doğrulama, sistemin "Sen kimsin?" sorusuna yanıt aradığı süreçtir. Bu uygulamada **JWT (JSON Web Token)** tabanlı, **stateless** bir yaklaşım kullanılmıştır.

### JWT Nedir?

JWT, üç bölümden oluşan imzalı bir token formatıdır:

```
HEADER.PAYLOAD.SIGNATURE

Örnek:
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZSI6IlVTRVIifQ.Xyz...
```

- **Header**: Algoritma bilgisi (`HS256`)
- **Payload**: Kullanıcı bilgileri (`sub`, `role`, `iat`, `exp`)
- **Signature**: HMAC-SHA256 imzası — token'ın değiştirilmediğini kanıtlar

Sunucu tarafında **oturum (session) tutulmaz**. Her istekte token doğrulanır.

### Akış

#### 1) Kayıt (`POST /api/auth/register`)

```
Frontend → { username, email, password }
    ↓
AuthService:
  - Kullanıcı adı/e-posta benzersizlik kontrolü
  - BCryptPasswordEncoder ile parola hashleme
  - User kaydı (rol: USER)
  - JWT üretimi (JwtService)
    ↓
Response → { token, username, role }
```

**Parola hiçbir zaman düz metin olarak saklanmaz.** BCrypt her hash'e rastgele bir "salt" ekler, bu yüzden aynı parola her seferinde farklı hash üretir.

#### 2) Giriş (`POST /api/auth/login`)

```
Frontend → { username, password }
    ↓
AuthenticationManager.authenticate()
  - CustomUserDetailsService.loadUserByUsername() → DB sorgusu
  - BCryptPasswordEncoder.matches(düzParola, hashParola)
  - Eşleşmezse: BadCredentialsException → 401
    ↓
Başarılı → JwtService.generateToken(user) → token
    ↓
Response → { token, username, role }
```

#### 3) Sonraki İstekler

```
Frontend → Authorization: Bearer <token>
    ↓
JwtAuthFilter:
  1. "Bearer " öneki kaldırılır
  2. JwtService.extractUsername(token)
  3. CustomUserDetailsService.loadUserByUsername()
  4. JwtService.isTokenValid(token, userDetails)
  5. SecurityContextHolder'a kimlik doğrulama eklenir
    ↓
Controller çalışır
```

### İlgili Sınıflar

| Sınıf | Dosya | Görev |
|---|---|---|
| `JwtService` | `security/JwtService.java` | Token üretme/doğrulama |
| `JwtAuthFilter` | `security/JwtAuthFilter.java` | Her istekte JWT kontrolü |
| `SecurityConfig` | `security/SecurityConfig.java` | Güvenlik kuralları |
| `AuthService` | `auth/AuthService.java` | Kayıt/giriş iş mantığı |
| `useAuthStore` | `src/stores/auth.js` | Frontend token yönetimi |
| `http.js` | `src/api/http.js` | Axios JWT interceptor |

---

## Yetkilendirme

> **Ödev Açıklaması — Authorization (Yetkilendirme)**

### Nedir?

Yetkilendirme, "Bu işlemi yapmaya hakkın var mı?" sorusuna yanıt aradığı süreçtir. Authentication başarılı olduktan sonra çalışır.

### Roller

| Rol | Açıklama |
|---|---|
| `USER` | Quiz çözebilir, geçmişini ve liderlik tablosunu görebilir |
| `ADMIN` | Tüm USER yetkileri + soru/quiz CRUD + tüm sonuçları görme |

### Backend Yetki Katmanları

**1) SecurityConfig — URL bazlı kural:**

```java
.requestMatchers("/api/auth/**").permitAll()       // herkese açık
.requestMatchers(HttpMethod.GET, "/api/quizzes/**").permitAll()
.requestMatchers("/api/admin/**").hasRole("ADMIN") // yalnızca admin
.anyRequest().authenticated()                       // diğerleri: giriş gerekli
```

**2) @PreAuthorize — Metot bazlı kural:**

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<QuestionResponse>> getAll() { ... }
```

Bu anotasyon `@EnableMethodSecurity` ile aktif edilir (`SecurityConfig.java`).

**3) Kullanıcı kısıtlaması — Servis katmanı:**

```java
// Kullanıcı yalnızca kendi denemesini görebilir
if (!attempt.getUser().getUsername().equals(username)) {
    throw new ApiException("Bu denemeye erişim yetkiniz yok", HttpStatus.FORBIDDEN);
}
```

### Frontend Yetki Kontrolü

**Route Guard** (`router/index.js`):

```js
if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
  return '/quizzes'   // admin değilse quiz listesine yönlendir
}
```

**Navbar'da koşullu render:**

```vue
<template v-if="authStore.isAdmin">
  <!-- Admin menü öğeleri -->
</template>
```

**Hata senaryoları:**

| Durum | HTTP Kodu | Frontend Tepkisi |
|---|---|---|
| Token yok | 401 | /login'e yönlendir (interceptor) |
| Yetersiz yetki | 403 | Hata mesajı göster |
| Token süresi doldu | 401 | Logout + /login |

---

## CRUD İşlemleri

> **Ödev Açıklaması — CRUD (Create, Read, Update, Delete)**

CRUD, veritabanı kayıtları üzerindeki dört temel işlemi ifade eder.

### Soru CRUD Örneği

#### CREATE — Yeni soru oluşturma

```http
POST /api/admin/questions
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "text": "Türkiye'nin başkenti neresidir?",
  "options": ["İstanbul", "İzmir", "Ankara", "Bursa"],
  "correctOptionIndex": 2,
  "difficulty": "EASY",
  "categoryId": 1
}
```

**Backend akışı:**
1. `@PreAuthorize("hasRole('ADMIN')")` → rol kontrolü
2. `@Valid @RequestBody QuestionRequest` → doğrulama (`@NotBlank`, `@Size`)
3. Kategori varlığı kontrolü
4. `Question` entity oluşturma
5. `questionRepository.save()` → SQL INSERT
6. `201 Created` + JSON yanıt

#### READ — Soru listeleme

```http
GET /api/admin/questions          # tüm sorular
GET /api/admin/questions?categoryId=1  # filtreli
GET /api/admin/questions/5        # tek soru
```

#### UPDATE — Soru güncelleme

```http
PUT /api/admin/questions/5
Authorization: Bearer <admin-token>

{
  "text": "Güncellenmiş soru metni",
  "options": ["A", "B", "C", "D"],
  "correctOptionIndex": 0,
  "difficulty": "MEDIUM",
  "categoryId": 2
}
```

**Backend akışı:**
1. ID ile mevcut soru yükleme (yoksa 404)
2. Tüm alanları güncelleme (tam güncelleme / PUT semantiği)
3. `questionRepository.save()` → SQL UPDATE

#### DELETE — Soru silme

```http
DELETE /api/admin/questions/5
Authorization: Bearer <admin-token>
```

**Backend akışı:**
1. Varlık kontrolü
2. `questionRepository.deleteById()` → SQL DELETE
3. `204 No Content` yanıt

### JPA / Hibernate Rol

Spring Data JPA, SQL'i otomatik üretir. Örnek:

```java
// Bu metot imzasından JPA otomatik SQL üretir:
List<Question> findByCategoryId(Long categoryId);
// → SELECT * FROM questions WHERE category_id = ?

// JPQL ile özel sorgu:
@Query("SELECT a FROM Attempt a WHERE a.score = (SELECT MAX(a2.score) ...")
List<Attempt> findTopScorePerUser();
```

---

## API Referansı

### Auth (Herkese Açık)

| Method | URL | Açıklama |
|---|---|---|
| POST | `/api/auth/register` | Kayıt |
| POST | `/api/auth/login` | Giriş (JWT döner) |

### Kullanıcı Endpoint'leri (Token Gerekli)

| Method | URL | Açıklama |
|---|---|---|
| GET | `/api/quizzes` | Quiz listesi |
| GET | `/api/quizzes?categoryId=1` | Kategori filtreli |
| GET | `/api/quizzes/{id}/play` | Quiz + sorular (cevaplar gizli) |
| GET | `/api/categories` | Kategori listesi |
| POST | `/api/attempts` | Quiz cevaplarını gönder |
| GET | `/api/attempts/history` | Kendi geçmişim |
| GET | `/api/attempts/{id}` | Tek deneme detayı |
| GET | `/api/leaderboard` | Liderlik tablosu |

### Admin Endpoint'leri (ADMIN Token Gerekli)

| Method | URL | Açıklama |
|---|---|---|
| GET | `/api/admin/questions` | Tüm sorular |
| POST | `/api/admin/questions` | Soru ekle |
| PUT | `/api/admin/questions/{id}` | Soru güncelle |
| DELETE | `/api/admin/questions/{id}` | Soru sil |
| GET | `/api/admin/quizzes` | Tüm quizler (admin) |
| POST | `/api/admin/quizzes` | Quiz oluştur |
| PUT | `/api/admin/quizzes/{id}` | Quiz güncelle |
| DELETE | `/api/admin/quizzes/{id}` | Quiz sil |
| POST | `/api/admin/categories` | Kategori ekle |
| PUT | `/api/admin/categories/{id}` | Kategori güncelle |
| DELETE | `/api/admin/categories/{id}` | Kategori sil |
| GET | `/api/admin/attempts` | Tüm kullanıcı denemeleri |

---

## Hazır Kullanıcılar (Seed)

Uygulama ilk başlangıçta otomatik olarak aşağıdaki verileri yükler:

| Kullanıcı | Parola | Rol |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `user` | `user123` | USER |
| `ahmet` | `ahmet123` | USER |

**Kategoriler:** Matematik, Tarih, Bilim  
**Sorular:** 15 soru (5 kolay, 5 orta, 5 zor)  
**Quizler:** 4 quiz (Matematik Temelleri, Türk Tarihi, Bilim Dünyası, Hızlı Matematik)

---

## Geliştirilebilir Özellikler

Ödev kapsamı dışında tutulmuş ancak eklenebilecek özellikler:

- **Refresh Token**: JWT süresi dolunca yeniden giriş gerektirmeden token yenileme
- **E-posta doğrulama**: Kayıt sonrası doğrulama e-postası
- **Şifre sıfırlama**: E-posta ile şifre sıfırlama akışı
- **Quiz istatistikleri**: Kategori bazlı performans analizi
- **Çoklu dil**: i18n ile Türkçe/İngilizce desteği
- **Rate limiting**: API brute-force koruması
