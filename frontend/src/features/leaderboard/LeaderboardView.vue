<template>
  <div class="max-w-4xl mx-auto px-4 py-8">

    <div class="mb-8">
      <h1 class="text-2xl font-semibold text-white">Liderlik Tablosu</h1>
      <p class="text-gray-400 text-sm mt-0.5">Her kullanıcının en yüksek skoru</p>
    </div>

    <div v-if="loading" class="flex justify-center py-16">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <template v-else>
      <!-- ECharts: En iyi 10 kullanıcı bar chart -->
      <div class="card mb-6" v-if="entries.length">
        <h2 class="text-sm font-medium text-gray-400 mb-4">Skor Dağılımı (İlk 10)</h2>
        <v-chart :option="chartOption" style="height: 260px;" autoresize />
      </div>

      <!-- Tablo -->
      <div class="card overflow-hidden p-0">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-white/10">
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">#</th>
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Kullanıcı</th>
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">En İyi Quiz</th>
              <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Puan</th>
              <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Deneme</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="entry in entries"
              :key="entry.rank"
              class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
              :class="{ 'bg-primary-500/5': entry.username === currentUser }"
            >
              <td class="px-5 py-3.5">
                <span v-if="entry.rank <= 3">
                  <font-awesome-icon icon="medal" :class="medalColor(entry.rank)" />
                </span>
                <span v-else class="text-gray-500">{{ entry.rank }}</span>
              </td>
              <td class="px-5 py-3.5 font-medium text-white">
                {{ entry.username }}
                <span v-if="entry.username === currentUser" class="badge bg-primary-500/20 text-primary-300 ml-1 text-xs">Sen</span>
              </td>
              <td class="px-5 py-3.5 text-gray-400 truncate max-w-[200px]">{{ entry.quizTitle }}</td>
              <td class="px-5 py-3.5 text-right font-semibold" :class="scoreColor(entry.bestScore)">
                {{ Math.round(entry.bestScore) }}%
              </td>
              <td class="px-5 py-3.5 text-right text-gray-500">{{ entry.totalAttempts }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import http from '@/api/http.js'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent])

const loading = ref(true)
const entries = ref([])
const currentUser = JSON.parse(localStorage.getItem('user') || 'null')?.username

onMounted(async () => {
  const { data } = await http.get('/leaderboard')
  entries.value = data
  loading.value = false
})

const chartOption = computed(() => {
  const top10 = entries.value.slice(0, 10)
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '3%', bottom: '3%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: top10.map(e => e.username),
      axisLabel: { color: '#9ca3af', rotate: 30 }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: { color: '#9ca3af', formatter: '{value}%' },
      splitLine: { lineStyle: { color: '#2a2a40' } }
    },
    series: [{
      type: 'bar',
      data: top10.map(e => ({
        value: Math.round(e.bestScore),
        itemStyle: {
          color: e.username === currentUser ? '#4f6ef7' : '#2a2a40',
          borderRadius: [4, 4, 0, 0]
        }
      }))
    }]
  }
})

function medalColor(rank) {
  return rank === 1 ? 'text-yellow-400' : rank === 2 ? 'text-gray-300' : 'text-amber-600'
}

function scoreColor(score) {
  if (score >= 80) return 'text-emerald-400'
  if (score >= 50) return 'text-yellow-400'
  return 'text-red-400'
}
</script>
