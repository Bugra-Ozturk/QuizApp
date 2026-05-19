/**
 * Auth Store (Pinia)
 *
 * Kullanıcı oturum durumunu merkezi olarak yönetir.
 * Token ve kullanıcı bilgisi localStorage'da tutulur —
 * sayfa yenilendiğinde oturum kaybolmaz.
 *
 * State:
 *   token    : JWT string veya null
 *   user     : { username, role } veya null
 *
 * Getters:
 *   isLoggedIn : oturum açık mı?
 *   isAdmin    : ADMIN rolü var mı?
 *
 * Actions:
 *   login(credentials)   → API çağrısı, token kaydet
 *   register(data)       → API çağrısı, kayıt + token kaydet
 *   logout()             → token temizle, /login'e yönlendir
 *   initFromStorage()    → sayfa yenilendiğinde store'u doldur
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '@/api/http.js'
import router from '@/router/index.js'

export const useAuthStore = defineStore('auth', () => {
  // ── State ──────────────────────────────────────────────────────────────────
  const token = ref(localStorage.getItem('token') || null)
  const user  = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // ── Getters ────────────────────────────────────────────────────────────────
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin    = computed(() => user.value?.role === 'ADMIN')

  // ── Actions ────────────────────────────────────────────────────────────────

  /** Giriş işlemi */
  async function login(credentials) {
    const { data } = await http.post('/auth/login', credentials)
    _saveSession(data)
  }

  /** Kayıt işlemi */
  async function register(userData) {
    const { data } = await http.post('/auth/register', userData)
    _saveSession(data)
  }

  /** Çıkış işlemi */
  function logout() {
    token.value = null
    user.value  = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }

  /** Token + kullanıcı bilgisini state ve localStorage'a yaz */
  function _saveSession(data) {
    token.value = data.token
    user.value  = { username: data.username, role: data.role }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return { token, user, isLoggedIn, isAdmin, login, register, logout }
})
