<template>
  <div class="max-w-3xl mx-auto px-4 py-8">

    <!-- Yükleniyor -->
    <div v-if="loading" class="flex justify-center py-20">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <!-- Quiz Arayüzü -->
    <template v-else-if="quiz && !submitted">

      <!-- Üst Bar: Başlık + Sayaç -->
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="font-semibold text-white">{{ quiz.title }}</h1>
          <p class="text-xs text-gray-400 mt-0.5">
            Soru {{ currentIndex + 1 }} / {{ quiz.questions.length }}
          </p>
        </div>

        <!-- Geri sayım sayacı — kırmızıya döner (süre < 30s) -->
        <div
          class="flex items-center gap-2 px-4 py-2 rounded-lg font-mono text-lg font-semibold"
          :class="timeLeft < 30 ? 'bg-red-500/20 text-red-400' : 'bg-surface-800 text-white'"
        >
          <font-awesome-icon icon="clock" class="text-sm" />
          {{ formatted }}
        </div>
      </div>

      <!-- İlerleme çubuğu -->
      <div class="h-1 bg-white/5 rounded-full mb-8 overflow-hidden">
        <div
          class="h-full bg-primary-500 rounded-full transition-all duration-300"
          :style="{ width: `${((currentIndex + 1) / quiz.questions.length) * 100}%` }"
        />
      </div>

      <!-- Soru -->
      <div class="card mb-4">
        <p class="text-white text-lg leading-relaxed">{{ currentQuestion.text }}</p>
      </div>

      <!-- Seçenekler -->
      <div class="space-y-2 mb-8">
        <button
          v-for="(option, idx) in currentQuestion.options"
          :key="idx"
          @click="selectAnswer(idx)"
          class="w-full text-left px-5 py-3.5 rounded-xl border transition-all duration-150 text-sm"
          :class="optionClass(idx)"
        >
          <span class="font-medium mr-3 opacity-50">{{ String.fromCharCode(65 + idx) }}.</span>
          {{ option }}
        </button>
      </div>

      <!-- Navigasyon -->
      <div class="flex justify-between">
        <button
          @click="prev"
          :disabled="currentIndex === 0"
          class="btn-secondary disabled:opacity-30"
        >
          <font-awesome-icon icon="chevron-left" class="mr-1" /> Önceki
        </button>

        <button
          v-if="currentIndex < quiz.questions.length - 1"
          @click="next"
          class="btn-primary"
        >
          Sonraki <font-awesome-icon icon="chevron-right" class="ml-1" />
        </button>

        <button v-else @click="finishQuiz" class="btn-primary">
          <font-awesome-icon icon="check" class="mr-1" /> Bitir
        </button>
      </div>

    </template>

    <!-- Gönderiliyor -->
    <div v-else-if="submitted" class="text-center py-20">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400 mb-3" />
      <p class="text-gray-400">Sonuçlar hesaplanıyor...</p>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import http from '@/api/http.js'
import { useCountdown } from '@/composables/useCountdown.js'

const route   = useRoute()
const router  = useRouter()
const toast   = useToast()

const loading      = ref(true)
const quiz         = ref(null)
const submitted    = ref(false)
const currentIndex = ref(0)
// answers[i] = seçilen indeks, -1 = cevaplanmadı
const answers      = ref([])
const startTime    = ref(Date.now())

// Countdown — quiz yüklenince başlar; süre dolunca otomatik gönder
let countdown = null

const currentQuestion = computed(() => quiz.value?.questions[currentIndex.value])

function optionClass(idx) {
  const selected = answers.value[currentIndex.value] === idx
  return selected
    ? 'border-primary-500 bg-primary-500/15 text-white'
    : 'border-white/10 bg-surface-800 text-gray-300 hover:border-white/20 hover:text-white'
}

function selectAnswer(idx) {
  answers.value[currentIndex.value] = idx
}

function next() { if (currentIndex.value < quiz.value.questions.length - 1) currentIndex.value++ }
function prev() { if (currentIndex.value > 0) currentIndex.value-- }

async function finishQuiz() {
  const unanswered = answers.value.filter(a => a === -1).length
  if (unanswered > 0) {
    const ok = confirm(`${unanswered} soru cevaplanmamış. Yine de bitirmek istiyor musunuz?`)
    if (!ok) return
  }
  await submitAnswers()
}

async function submitAnswers() {
  submitted.value = true
  countdown?.stop()
  const elapsed = Math.round((Date.now() - startTime.value) / 1000)

  try {
    const { data } = await http.post('/attempts', {
      quizId: quiz.value.id,
      answers: answers.value,
      durationSeconds: elapsed
    })
    router.push(`/results/${data.id}`)
  } catch {
    toast.error('Sonuç kaydedilirken hata oluştu.')
    submitted.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await http.get(`/quizzes/${route.params.id}/play`)
    quiz.value = data
    // Her soruya -1 (cevaplanmadı) ile başla
    answers.value = new Array(data.questions.length).fill(-1)
    loading.value = false
    startTime.value = Date.now()

    // Geri sayımı başlat — süre dolunca otomatik gönder
    countdown = useCountdown(data.timeLimitSeconds, () => {
      toast.warning('Süre doldu! Cevaplarınız gönderiliyor...')
      submitAnswers()
    })
    countdown.start()
  } catch {
    toast.error('Quiz yüklenemedi.')
    router.push('/quizzes')
  }
})

// Şablon için countdown reaktif değerlerini doğrudan aç
const timeLeft  = computed(() => countdown?.timeLeft.value ?? 0)
const formatted = computed(() => countdown?.formatted.value ?? '00:00')
</script>
