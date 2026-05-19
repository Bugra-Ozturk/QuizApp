/**
 * Merkezi axios instance.
 *
 * DRY prensibi: tüm API çağrıları bu instance üzerinden yapılır.
 * İki interceptor tanımlanmıştır:
 *
 * 1) Request interceptor:
 *    - Her istekte localStorage'dan JWT token'ı okur
 *    - "Authorization: Bearer <token>" başlığını otomatik ekler
 *    - Her endpoint'te manuel başlık yazmaya gerek kalmaz
 *
 * 2) Response interceptor:
 *    - 401 Unauthorized gelirse kullanıcıyı logout eder ve /login'e yönlendirir
 *    - Token süresi dolmuşsa veya geçersizse oturumu temizler
 */

import axios from 'axios'
import router from '@/router/index.js'

const http = axios.create({
  baseURL: '/api',          // Vite proxy (geliştirme) / Caddy reverse proxy (üretim)
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// ── Request Interceptor: JWT token otomatik ekleme ──────────────────────────
http.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ── Response Interceptor: 401 otomatik logout ────────────────────────────────
http.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token geçersiz veya süresi dolmuş — oturumu temizle
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default http
