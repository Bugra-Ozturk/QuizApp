<template>
  <div class="max-w-6xl mx-auto px-4 py-8">

    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-semibold text-white">Quiz Yönetimi</h1>
        <p class="text-gray-400 text-sm mt-0.5">Quiz oluştur, düzenle veya sil</p>
      </div>
      <button @click="openCreate" class="btn-primary text-sm">
        <font-awesome-icon icon="plus" class="mr-1.5" /> Yeni Quiz
      </button>
    </div>

    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-white/10">
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Başlık</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Kategori</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Soru</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Süre</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">İşlem</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="px-5 py-8 text-center text-gray-500">
              <font-awesome-icon icon="spinner" spin class="mr-2" /> Yükleniyor...
            </td>
          </tr>
          <tr
            v-for="quiz in quizzes"
            :key="quiz.id"
            class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
          >
            <td class="px-5 py-3.5 font-medium text-gray-200">{{ quiz.title }}</td>
            <td class="px-5 py-3.5">
              <span class="badge bg-primary-500/15 text-primary-300">{{ quiz.categoryName }}</span>
            </td>
            <td class="px-5 py-3.5 text-gray-400">{{ quiz.questionCount }}</td>
            <td class="px-5 py-3.5 text-gray-400">{{ formatTime(quiz.timeLimitSeconds) }}</td>
            <td class="px-5 py-3.5 text-right">
              <button @click="openEdit(quiz)" class="text-gray-400 hover:text-white mr-3 transition-colors">
                <font-awesome-icon icon="edit" />
              </button>
              <button @click="deleteQuiz(quiz.id)" class="text-gray-400 hover:text-red-400 transition-colors">
                <font-awesome-icon icon="trash" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <QuizModal
      v-if="showModal"
      :quiz="editingQuiz"
      :categories="categories"
      :all-questions="allQuestions"
      @save="saveQuiz"
      @close="showModal = false"
    />

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import http from '@/api/http.js'
import QuizModal from './QuizModal.vue'

const toast         = useToast()
const loading       = ref(true)
const quizzes       = ref([])
const categories    = ref([])
const allQuestions  = ref([])
const showModal     = ref(false)
const editingQuiz   = ref(null)

onMounted(async () => {
  const [qRes, cRes, qstRes] = await Promise.all([
    http.get('/quizzes'),
    http.get('/categories'),
    http.get('/admin/questions')
  ])
  quizzes.value      = qRes.data
  categories.value   = cRes.data
  allQuestions.value = qstRes.data
  loading.value      = false
})

function openCreate() { editingQuiz.value = null; showModal.value = true }
function openEdit(q)  { editingQuiz.value = { ...q }; showModal.value = true }

async function saveQuiz(payload) {
  try {
    if (payload.id) {
      const { data } = await http.put(`/admin/quizzes/${payload.id}`, payload)
      const idx = quizzes.value.findIndex(q => q.id === payload.id)
      if (idx !== -1) quizzes.value[idx] = data
      toast.success('Quiz güncellendi.')
    } else {
      const { data } = await http.post('/admin/quizzes', payload)
      quizzes.value.unshift(data)
      toast.success('Quiz oluşturuldu.')
    }
    showModal.value = false
  } catch (err) {
    toast.error(err.response?.data?.message || 'İşlem başarısız.')
  }
}

async function deleteQuiz(id) {
  if (!confirm('Bu quizi silmek istediğinize emin misiniz?')) return
  try {
    await http.delete(`/admin/quizzes/${id}`)
    quizzes.value = quizzes.value.filter(q => q.id !== id)
    toast.success('Quiz silindi.')
  } catch {
    toast.error('Silme işlemi başarısız.')
  }
}

function formatTime(s) {
  const m = Math.floor(s / 60); const sec = s % 60
  return sec === 0 ? `${m} dk` : `${m}:${String(sec).padStart(2,'0')}`
}
</script>
