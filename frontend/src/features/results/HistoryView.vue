<template>
  <div class="max-w-4xl mx-auto px-4 py-8">

    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-2xl font-semibold text-white">Geçmişim</h1>
        <p class="text-gray-400 text-sm mt-0.5">Önceki quiz sonuçlarınız</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-16">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <div v-else-if="history.length" class="space-y-3">
      <RouterLink
        v-for="attempt in history"
        :key="attempt.id"
        :to="`/results/${attempt.id}`"
        class="card flex items-center justify-between hover:border-white/15 transition-colors duration-150 no-underline"
      >
        <div class="flex-1 min-w-0">
          <p class="font-medium text-white truncate">{{ attempt.quizTitle }}</p>
          <p class="text-xs text-gray-500 mt-0.5">{{ formatDate(attempt.createdAt) }}</p>
        </div>

        <div class="flex items-center gap-6 ml-4">
          <div class="text-right">
            <p class="text-sm font-semibold" :class="scoreColor(attempt.score)">
              {{ Math.round(attempt.score) }}%
            </p>
            <p class="text-xs text-gray-500">{{ attempt.correctCount }}/{{ attempt.totalQuestions }}</p>
          </div>
          <font-awesome-icon icon="chevron-right" class="text-gray-600" />
        </div>
      </RouterLink>
    </div>

    <div v-else class="text-center py-20 text-gray-500">
      <font-awesome-icon icon="history" class="text-4xl mb-3 opacity-30" />
      <p>Henüz quiz çözmediniz.</p>
      <RouterLink to="/quizzes" class="btn-primary inline-block mt-4 text-sm">Quizlere Git</RouterLink>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import http from '@/api/http.js'

const loading = ref(true)
const history = ref([])

onMounted(async () => {
  const { data } = await http.get('/attempts/history')
  history.value = data
  loading.value = false
})

function scoreColor(score) {
  if (score >= 80) return 'text-emerald-400'
  if (score >= 50) return 'text-yellow-400'
  return 'text-red-400'
}

function formatDate(dt) {
  return new Date(dt).toLocaleString('tr-TR', { dateStyle: 'medium', timeStyle: 'short' })
}
</script>
