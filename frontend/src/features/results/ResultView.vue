<template>
  <div class="max-w-3xl mx-auto px-4 py-8">

    <div v-if="loading" class="flex justify-center py-20">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <template v-else-if="attempt">

      <!-- Özet Kartı -->
      <div class="card mb-6 text-center">
        <!-- Puan dairesi -->
        <div class="inline-flex items-center justify-center w-24 h-24 rounded-full mb-4"
             :class="scoreClass.bg">
          <span class="text-3xl font-bold" :class="scoreClass.text">
            {{ Math.round(attempt.score) }}%
          </span>
        </div>

        <h1 class="text-xl font-semibold text-white">{{ attempt.quizTitle }}</h1>
        <p class="text-gray-400 text-sm mt-1">{{ formatDate(attempt.createdAt) }}</p>

        <!-- İstatistikler -->
        <div class="flex justify-center gap-8 mt-6 pt-6 border-t border-white/10">
          <Stat icon="check" color="text-emerald-400" :value="attempt.correctCount" label="Doğru" />
          <Stat icon="times" color="text-red-400" :value="attempt.wrongCount" label="Yanlış" />
          <Stat icon="clock" color="text-blue-400" :value="formatDuration(attempt.durationSeconds)" label="Süre" />
        </div>
      </div>

      <!-- ECharts: Doğru/Yanlış pasta grafiği -->
      <div class="card mb-6">
        <h2 class="font-medium text-white mb-4">Sonuç Dağılımı</h2>
        <v-chart :option="chartOption" style="height: 220px;" autoresize />
      </div>

      <!-- Soru detayları -->
      <div class="card">
        <h2 class="font-medium text-white mb-4">Soru Detayları</h2>
        <div class="space-y-4">
          <div
            v-for="(ans, i) in attempt.answers"
            :key="ans.questionId"
            class="p-4 rounded-lg border"
            :class="ans.isCorrect ? 'border-emerald-500/25 bg-emerald-500/5' : 'border-red-500/25 bg-red-500/5'"
          >
            <div class="flex items-start gap-2">
              <font-awesome-icon
                :icon="ans.isCorrect ? 'check' : 'times'"
                class="mt-0.5 flex-shrink-0"
                :class="ans.isCorrect ? 'text-emerald-400' : 'text-red-400'"
              />
              <div class="flex-1">
                <p class="text-sm text-gray-200 font-medium">{{ i + 1 }}. {{ ans.questionText }}</p>
                <div class="mt-2 space-y-1">
                  <p v-for="(opt, idx) in ans.options" :key="idx"
                     class="text-xs px-2 py-1 rounded"
                     :class="optClass(idx, ans)">
                    {{ String.fromCharCode(65 + idx) }}. {{ opt }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Aksiyonlar -->
      <div class="flex gap-3 mt-6">
        <RouterLink to="/quizzes" class="btn-secondary flex-1 text-center">
          <font-awesome-icon icon="list" class="mr-1" /> Quizler
        </RouterLink>
        <RouterLink to="/leaderboard" class="btn-primary flex-1 text-center">
          <font-awesome-icon icon="trophy" class="mr-1" /> Liderlik Tablosu
        </RouterLink>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import http from '@/api/http.js'
import Stat from './Stat.vue'

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent])

const route   = useRoute()
const loading = ref(true)
const attempt = ref(null)

onMounted(async () => {
  const { data } = await http.get(`/attempts/${route.params.id}`)
  attempt.value = data
  loading.value = false
})

const scoreClass = computed(() => {
  const s = attempt.value?.score ?? 0
  if (s >= 80) return { bg: 'bg-emerald-500/15', text: 'text-emerald-400' }
  if (s >= 50) return { bg: 'bg-yellow-500/15', text: 'text-yellow-400' }
  return { bg: 'bg-red-500/15', text: 'text-red-400' }
})

const chartOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0, textStyle: { color: '#9ca3af' } },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    label: { show: false },
    data: [
      { value: attempt.value?.correctCount, name: 'Doğru', itemStyle: { color: '#10b981' } },
      { value: attempt.value?.wrongCount,   name: 'Yanlış', itemStyle: { color: '#ef4444' } }
    ]
  }]
}))

function optClass(idx, ans) {
  if (idx === ans.correctOptionIndex) return 'bg-emerald-500/20 text-emerald-300'
  if (idx === ans.selectedOptionIndex && !ans.isCorrect) return 'bg-red-500/20 text-red-300'
  return 'text-gray-500'
}

function formatDate(dt) {
  return new Date(dt).toLocaleString('tr-TR')
}

function formatDuration(s) {
  const m = Math.floor(s / 60)
  const sec = s % 60
  return `${m}:${String(sec).padStart(2,'0')}`
}
</script>
