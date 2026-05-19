<template>
  <div class="max-w-4xl mx-auto px-4 py-8">

    <!-- Başlık -->
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-white">Liderlik Tablosu</h1>
      <p class="text-gray-400 text-sm mt-0.5">En başarılı kullanıcılar</p>
    </div>

    <!-- Tab + Quiz Seçici -->
    <div class="flex flex-col sm:flex-row sm:items-center gap-3 mb-6">

      <!-- Tab -->
      <div class="flex bg-surface-800 border border-white/10 rounded-lg p-1 gap-1">
        <button
          @click="switchMode('global')"
          class="flex-1 px-4 py-1.5 rounded-md text-sm font-medium transition-colors duration-150"
          :class="mode === 'global'
            ? 'bg-primary-500 text-white shadow'
            : 'text-gray-400 hover:text-white'"
        >
          <font-awesome-icon icon="chart-bar" class="mr-1.5 text-xs" />
          Genel
        </button>
        <button
          @click="switchMode('quiz')"
          class="flex-1 px-4 py-1.5 rounded-md text-sm font-medium transition-colors duration-150"
          :class="mode === 'quiz'
            ? 'bg-primary-500 text-white shadow'
            : 'text-gray-400 hover:text-white'"
        >
          <font-awesome-icon icon="list" class="mr-1.5 text-xs" />
          Quiz Bazlı
        </button>
      </div>

      <!-- Quiz seçici (yalnızca quiz modunda) -->
      <Transition name="fade">
        <select
          v-if="mode === 'quiz'"
          v-model="selectedQuizId"
          class="input !w-auto sm:min-w-[220px]"
          @change="load"
        >
          <option value="">Quiz seçin...</option>
          <option v-for="q in quizzes" :key="q.id" :value="q.id">{{ q.title }}</option>
        </select>
      </Transition>

      <!-- Skor etiketi -->
      <span class="text-xs text-gray-500 sm:ml-auto">
        {{ mode === 'global' ? 'Sıralama: Ortalama başarı %' : 'Sıralama: Quiz en yüksek skor' }}
      </span>
    </div>

    <!-- Yükleniyor -->
    <div v-if="loading" class="flex justify-center py-16">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <!-- Boş durum (quiz modu, quiz seçilmedi) -->
    <div v-else-if="mode === 'quiz' && !selectedQuizId" class="text-center py-20 text-gray-500">
      <font-awesome-icon icon="list" class="text-4xl mb-3 opacity-20" />
      <p class="text-sm">Liderlik tablosunu görmek için bir quiz seçin.</p>
    </div>

    <!-- Boş durum (veri yok) -->
    <div v-else-if="!entries.length" class="text-center py-20 text-gray-500">
      <font-awesome-icon icon="trophy" class="text-4xl mb-3 opacity-20" />
      <p class="text-sm">Henüz sonuç yok.</p>
    </div>

    <template v-else>
      <!-- ECharts Bar Grafik -->
      <div class="card mb-5" v-if="entries.length">
        <p class="text-xs font-medium text-gray-500 mb-3">
          {{ mode === 'global' ? 'Ortalama Başarı (İlk 10)' : 'En Yüksek Skor (İlk 10)' }}
        </p>
        <v-chart :option="chartOption" style="height: 240px;" autoresize />
      </div>

      <!-- Podyum (ilk 3) -->
      <div v-if="entries.length >= 3" class="grid grid-cols-3 gap-3 mb-5">
        <!-- 2. -->
        <PodiumCard :entry="entries[1]" :current-user="currentUser" place="2" />
        <!-- 1. -->
        <PodiumCard :entry="entries[0]" :current-user="currentUser" place="1" class="sm:-mt-4" />
        <!-- 3. -->
        <PodiumCard :entry="entries[2]" :current-user="currentUser" place="3" />
      </div>

      <!-- Tablo (4. ve sonrası) -->
      <div v-if="entries.length > 3" class="card overflow-hidden p-0">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-white/10">
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">#</th>
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Kullanıcı</th>
              <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">
                {{ mode === 'global' ? 'En İyi Quiz' : 'Quiz' }}
              </th>
              <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">
                {{ mode === 'global' ? 'Ort. Puan' : 'En Yüksek' }}
              </th>
              <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">Deneme</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="entry in entries.slice(3)"
              :key="entry.rank"
              class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
              :class="{ 'bg-primary-500/5': entry.username === currentUser }"
            >
              <td class="px-5 py-3.5 text-gray-500 text-sm">{{ entry.rank }}</td>
              <td class="px-5 py-3.5 font-medium text-white">
                {{ entry.username }}
                <span v-if="entry.username === currentUser"
                  class="badge bg-primary-500/20 text-primary-300 ml-1 text-[10px]">Sen</span>
              </td>
              <td class="px-5 py-3.5 text-gray-400 truncate max-w-[180px]">{{ entry.bestQuizTitle }}</td>
              <td class="px-5 py-3.5 text-right font-semibold" :class="scoreColor(entry.avgScore)">
                {{ Math.round(entry.avgScore) }}%
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
import PodiumCard from './PodiumCard.vue'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent])

const loading       = ref(false)
const entries       = ref([])
const quizzes       = ref([])
const mode          = ref('global')       // 'global' | 'quiz'
const selectedQuizId = ref('')
const currentUser   = JSON.parse(localStorage.getItem('user') || 'null')?.username

onMounted(async () => {
  const [, quizRes] = await Promise.all([load(), http.get('/quizzes')])
  quizzes.value = quizRes.data
})

async function load() {
  if (mode.value === 'quiz' && !selectedQuizId.value) {
    entries.value = []
    return
  }
  loading.value = true
  const url = mode.value === 'quiz'
    ? `/leaderboard?quizId=${selectedQuizId.value}`
    : '/leaderboard'
  const { data } = await http.get(url)
  entries.value  = data
  loading.value  = false
}

function switchMode(m) {
  mode.value = m
  if (m === 'global') load()
  else entries.value = []
}

const chartOption = computed(() => {
  const top10 = entries.value.slice(0, 10)
  return {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (p) => `${p[0].name}<br/>${Math.round(p[0].value)}%`
    },
    grid: { left: '2%', right: '2%', bottom: '10%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: top10.map(e => e.username),
      axisLabel: { color: '#6b7280', fontSize: 11, rotate: top10.length > 5 ? 20 : 0 }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: { color: '#6b7280', formatter: '{value}%', fontSize: 11 },
      splitLine: { lineStyle: { color: '#1f2937' } }
    },
    series: [{
      type: 'bar',
      barMaxWidth: 48,
      data: top10.map(e => ({
        value: Math.round(e.avgScore),
        itemStyle: {
          color: e.username === currentUser ? '#4f6ef7' : '#374151',
          borderRadius: [4, 4, 0, 0]
        }
      }))
    }]
  }
})

function scoreColor(s) {
  return s >= 80 ? 'text-emerald-400' : s >= 50 ? 'text-yellow-400' : 'text-red-400'
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.15s, transform 0.15s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
