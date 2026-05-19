/**
 * Vue Router — SPA yönlendirme yapılandırması
 *
 * Route Guard (beforeEach):
 *   Her navigasyonda çalışır ve şu kuralları uygular:
 *
 *   requiresAuth: true  → giriş yapılmamışsa /login'e yönlendir
 *   requiresAdmin: true → ADMIN değilse /quizzes'e yönlendir
 *   guestOnly: true     → zaten giriş yapılmışsa /quizzes'e yönlendir
 *                         (giriş sayfasına tekrar gitmeyi engeller)
 */

import { createRouter, createWebHistory } from 'vue-router'

// Auth
const LoginView    = () => import('@/features/auth/LoginView.vue')
const RegisterView = () => import('@/features/auth/RegisterView.vue')

// Quiz
const QuizListView = () => import('@/features/quiz/QuizListView.vue')
const QuizPlayView = () => import('@/features/quiz/QuizPlayView.vue')

// Results
const ResultView  = () => import('@/features/results/ResultView.vue')
const HistoryView = () => import('@/features/results/HistoryView.vue')

// Leaderboard
const LeaderboardView = () => import('@/features/leaderboard/LeaderboardView.vue')

// Admin
const AdminQuestionView = () => import('@/features/admin/AdminQuestionView.vue')
const AdminQuizView     = () => import('@/features/admin/AdminQuizView.vue')
const AdminResultsView  = () => import('@/features/admin/AdminResultsView.vue')

const routes = [
  { path: '/', redirect: '/quizzes' },

  // ── Genel (giriş gerekmez) ─────────────────────────────────────────────────
  { path: '/login',    component: LoginView,    meta: { guestOnly: true } },
  { path: '/register', component: RegisterView, meta: { guestOnly: true } },

  // ── Kullanıcı ──────────────────────────────────────────────────────────────
  { path: '/quizzes',           component: QuizListView,   meta: { requiresAuth: true } },
  { path: '/quizzes/:id/play',  component: QuizPlayView,   meta: { requiresAuth: true } },
  { path: '/results/:id',       component: ResultView,     meta: { requiresAuth: true } },
  { path: '/history',           component: HistoryView,    meta: { requiresAuth: true } },
  { path: '/leaderboard',       component: LeaderboardView, meta: { requiresAuth: true } },

  // ── Admin ──────────────────────────────────────────────────────────────────
  { path: '/admin/questions', component: AdminQuestionView, meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/admin/quizzes',   component: AdminQuizView,    meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/admin/results',   component: AdminResultsView, meta: { requiresAuth: true, requiresAdmin: true } },

  // 404
  { path: '/:pathMatch(.*)*', redirect: '/quizzes' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// ── Global Route Guard ─────────────────────────────────────────────────────
router.beforeEach(to => {
  const token = localStorage.getItem('token')
  const user  = JSON.parse(localStorage.getItem('user') || 'null')

  // Yalnızca misafire açık sayfa (login/register) — giriş yapılmışsa yönlendir
  if (to.meta.guestOnly && token) {
    return '/quizzes'
  }

  // Kimlik doğrulama gerektiren sayfa
  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  // Admin yetkisi gerektiren sayfa
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    return '/quizzes'
  }
})

export default router
