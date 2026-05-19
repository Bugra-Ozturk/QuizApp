# ============================================================
# Frontend Dockerfile — Multi-stage build
#
# Aşama 1 (builder): Node.js ile npm install + vite build
# Aşama 2 (runtime): Caddy + build çıktısı
#
# Son imaj ~50MB — yalnızca Caddy binary ve dist/ klasörü içerir.
# ============================================================

# ── Aşama 1: Derleme ──────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Önce sadece package.json'ı kopyala (layer cache için)
COPY package.json ./
RUN npm install --silent

# Kaynak kodları kopyala ve Vite ile derle
COPY . .
RUN npm run build
# dist/ klasörü oluşur — üretim için optimize edilmiş HTML/JS/CSS

# ── Aşama 2: Caddy sunucusu ───────────────────────────────────────────────────
FROM caddy:2-alpine

# Vite build çıktısını Caddy'nin beklediği konuma kopyala
COPY --from=builder /app/dist /usr/share/caddy

# Caddy yapılandırma dosyasını kopyala
COPY Caddyfile /etc/caddy/Caddyfile

EXPOSE 80

# Caddy varsayılan ENTRYPOINT olarak başlar (CMD gerekmez)
