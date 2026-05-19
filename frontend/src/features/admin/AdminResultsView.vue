<template>
  <div class="max-w-6xl mx-auto px-4 py-8">

    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-white">Kullanıcı Sonuçları</h1>
      <p class="text-gray-400 text-sm mt-0.5">Tüm quiz denemeleri</p>
    </div>

    <!-- İstatistik özeti -->
    <div class="grid grid-cols-3 gap-4 mb-6" v-if="!loading">
      <div class="card text-center">
        <p class="text-2xl font-bold text-primary-400">{{ attempts.length }}</p>
        <p class="text-xs text-gray-400 mt-1">Toplam Deneme</p>
      </div>
      <div class="card text-center">
        <p class="text-2xl font-bold text-emerald-400">{{ avgScore }}%</p>
        <p class="text-xs text-gray-400 mt-1">Ortalama Puan</p>
      </div>
      <div class="card text-center">
        <p class="text-2xl font-bold text-blue-400">{{ uniqueUsers }}</p>
        <p class="text-xs text-gray-400 mt-1">Benzersiz Kullanıcı</p>
      </div>
    </div>

    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-white/10">
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Kullanıcı</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Quiz</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Puan</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Doğru/Toplam</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Süre</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Tarih</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="px-5 py-8 text-center text-gray-500">
              <font-awesome-icon icon="spinner" spin class="mr-2" /> Yükleniyor...
            </td>
          </tr>
          <tr
            v-for="a in attempts"
            :key="a.id"
            class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
          >
            <td class="px-5 py-3.5 font-medium text-gray-200">{{ a.quizTitle }}</td>
            <td class="px-5 py-3.5 text-gray-400 max-w-[200px] truncate">{{ a.quizTitle }}</td>
            <td class="px-5 py-3.5 text-right font-semibold" :class="scoreColor(a.score)">
              {{ Math.round(a.score) }}%
            </td>
            <td class="px-5 py-3.5 text-right text-gray-400">
              {{ a.correctCount }}/{{ a.totalQuestions }}
            </td>
            <td class="px-5 py-3.5 text-right text-gray-400">{{ formatDuration(a.durationSeconds) }}</td>
            <td class="px-5 py-3.5 text-right text-gray-500 text-xs">{{ formatDate(a.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import http from '@/api/http.js'

const loading  = ref(true)
const attempts = ref([])

onMounted(async () => {
  const { data } = await http.get('/admin/attempts')
  attempts.value = data
  loading.value  = false
})

const avgScore = computed(() => {
  if (!attempts.value.length) return 0
  return Math.round(attempts.value.reduce((sum, a) => sum + a.score, 0) / attempts.value.length)
})

const uniqueUsers = computed(() =>
  new Set(attempts.value.map(a => a.quizTitle)).size
)

function scoreColor(s) {
  return s >= 80 ? 'text-emerald-400' : s >= 50 ? 'text-yellow-400' : 'text-red-400'
}

function formatDuration(s) {
  const m = Math.floor(s / 60); const sec = s % 60
  return `${m}:${String(sec).padStart(2,'0')}`
}

function formatDate(dt) {
  return new Date(dt).toLocaleString('tr-TR', { dateStyle: 'short', timeStyle: 'short' })
}
</script>
