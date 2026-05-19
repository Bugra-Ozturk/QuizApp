<template>
  <div class="min-h-screen flex flex-col">
    <AppNavbar v-if="authStore.isLoggedIn" />
    <main class="flex-1">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import AppNavbar from '@/components/AppNavbar.vue'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()
</script>
