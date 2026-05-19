<template>
  <nav class="border-b border-white/10 bg-surface-800/80 backdrop-blur-sm sticky top-0 z-50">
    <div class="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">

      <!-- Logo -->
      <RouterLink to="/quizzes" class="flex items-center gap-2 text-white font-semibold text-lg">
        <font-awesome-icon icon="book-open" class="text-primary-500" />
        QuizApp
      </RouterLink>

      <!-- Ana Navigasyon -->
      <div class="flex items-center gap-1">
        <NavLink to="/quizzes" icon="list">Quizler</NavLink>
        <NavLink to="/history" icon="history">Geçmiş</NavLink>
        <NavLink to="/leaderboard" icon="trophy">Liderlik</NavLink>

        <!-- Admin menü -->
        <template v-if="authStore.isAdmin">
          <span class="w-px h-5 bg-white/10 mx-1" />
          <NavLink to="/admin/questions" icon="question-circle">Sorular</NavLink>
          <NavLink to="/admin/quizzes" icon="cog">Quizler (A)</NavLink>
          <NavLink to="/admin/results" icon="chart-bar">Sonuçlar</NavLink>
        </template>
      </div>

      <!-- Kullanıcı -->
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-400">
          <font-awesome-icon icon="user" class="mr-1" />
          {{ authStore.user?.username }}
          <span v-if="authStore.isAdmin" class="badge bg-primary-500/20 text-primary-300 ml-1">Admin</span>
        </span>
        <button @click="authStore.logout()" class="btn-secondary !px-3 !py-1.5 text-sm">
          <font-awesome-icon icon="sign-out-alt" />
        </button>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import NavLink from './NavLink.vue'

const authStore = useAuthStore()
</script>
