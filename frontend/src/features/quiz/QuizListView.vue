<template>
  <div class="max-w-6xl mx-auto px-4 py-8">

    <!-- Başlık + Filtre -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-semibold text-white">Quizler</h1>
        <p class="text-gray-400 text-sm mt-0.5">Kendinizi sınayın</p>
      </div>

      <!-- Kategori filtresi -->
      <select
        v-model="selectedCategory"
        class="input !w-auto min-w-[160px]"
        @change="filterQuizzes"
      >
        <option value="">Tüm Kategoriler</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>
    </div>

    <!-- Yükleniyor -->
    <div v-if="loading" class="flex justify-center py-16">
      <font-awesome-icon icon="spinner" spin class="text-3xl text-primary-400" />
    </div>

    <!-- Quiz listesi -->
    <div v-else-if="quizzes.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <QuizCard
        v-for="quiz in quizzes"
        :key="quiz.id"
        :quiz="quiz"
        @play="goPlay"
      />
    </div>

    <!-- Boş durum -->
    <div v-else class="text-center py-20 text-gray-500">
      <font-awesome-icon icon="list" class="text-4xl mb-3 opacity-30" />
      <p>Bu kategoride henüz quiz yok.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/http.js'
import QuizCard from './QuizCard.vue'

const router          = useRouter()
const loading         = ref(true)
const quizzes         = ref([])
const categories      = ref([])
const selectedCategory = ref('')

onMounted(async () => {
  const [quizRes, catRes] = await Promise.all([
    http.get('/quizzes'),
    http.get('/categories')
  ])
  quizzes.value    = quizRes.data
  categories.value = catRes.data
  loading.value    = false
})

async function filterQuizzes() {
  loading.value = true
  const url = selectedCategory.value ? `/quizzes?categoryId=${selectedCategory.value}` : '/quizzes'
  const { data } = await http.get(url)
  quizzes.value = data
  loading.value = false
}

function goPlay(id) {
  router.push(`/quizzes/${id}/play`)
}
</script>
