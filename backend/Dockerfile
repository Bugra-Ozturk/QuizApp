# ============================================================
# Backend Dockerfile — Multi-stage build
#
# Aşama 1 (builder): Maven ile uygulamayı derle → .jar üret
# Aşama 2 (runtime): Yalnızca JRE + .jar dosyasını kopyala
#
# Multi-stage build avantajı:
#   - Maven, JDK, kaynak kodlar son imajda yer almaz
#   - İmaj boyutu ~700MB → ~200MB'a düşer
# ============================================================

# ── Aşama 1: Derleme ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Önce bağımlılık tanımlamalarını kopyala (Docker layer cache'i verimli kullan)
# pom.xml değişmezse bağımlılıklar tekrar indirilmez
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Kaynak kodları kopyala ve derle (testleri atla — CI ortamında çalıştırılır)
COPY src ./src
RUN mvn package -DskipTests -q

# ── Aşama 2: Çalışma ortamı ───────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Güvenlik: root olmayan kullanıcı ile çalıştır
RUN addgroup -S quizapp && adduser -S quizapp -G quizapp
USER quizapp

# Derlenmiş JAR dosyasını kopyala
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Spring Boot uygulamasını başlat
ENTRYPOINT ["java", "-jar", "app.jar"]
