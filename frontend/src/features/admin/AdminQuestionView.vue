<template>
  <div class="max-w-6xl mx-auto px-4 py-8">

    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-semibold text-white">Soru Yönetimi</h1>
        <p class="text-gray-400 text-sm mt-0.5">Soru ekle, düzenle veya sil</p>
      </div>
      <button @click="openCreate" class="btn-primary text-sm">
        <font-awesome-icon icon="plus" class="mr-1.5" /> Yeni Soru
      </button>
    </div>

    <!-- Filtre -->
    <div class="flex gap-3 mb-6">
      <select v-model="filterCategory" class="input !w-auto" @change="loadQuestions">
        <option value="">Tüm Kategoriler</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
    </div>

    <!-- Tablo -->
    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-white/10">
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Soru</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Kategori</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Zorluk</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">İşlem</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="4" class="px-5 py-8 text-center text-gray-500">
              <font-awesome-icon icon="spinner" spin class="mr-2" /> Yükleniyor...
            </td>
          </tr>
          <tr
            v-for="q in questions"
            :key="q.id"
            class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
          >
            <td class="px-5 py-3.5 text-gray-200 max-w-[320px] truncate">{{ q.text }}</td>
            <td class="px-5 py-3.5">
              <span class="badge bg-primary-500/15 text-primary-300">{{ q.categoryName }}</span>
            </td>
            <td class="px-5 py-3.5">
              <span class="badge text-xs" :class="difficultyClass(q.difficulty)">{{ q.difficulty }}</span>
            </td>
            <td class="px-5 py-3.5 text-right">
              <button @click="openEdit(q)" class="text-gray-400 hover:text-white mr-3 transition-colors">
                <font-awesome-icon icon="edit" />
              </button>
              <button @click="deleteQuestion(q.id)" class="text-gray-400 hover:text-red-400 transition-colors">
                <font-awesome-icon icon="trash" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal: Soru Formu -->
    <QuestionModal
      v-if="showModal"
      :question="editingQuestion"
      :categories="categories"
      @save="saveQuestion"
      @close="showModal = false"
    />

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import http from '@/api/http.js'
import QuestionModal from './QuestionModal.vue'

const toast          = useToast()
const loading        = ref(true)
const questions      = ref([])
const categories     = ref([])
const filterCategory = ref('')
const showModal      = ref(false)
const editingQuestion = ref(null)

onMounted(async () => {
  const [qRes, cRes] = await Promise.all([http.get('/admin/questions'), http.get('/categories')])
  questions.value  = qRes.data
  categories.value = cRes.data
  loading.value    = false
})

async function loadQuestions() {
  loading.value = true
  const url = filterCategory.value
    ? `/admin/questions?categoryId=${filterCategory.value}`
    : '/admin/questions'
  const { data } = await http.get(url)
  questions.value = data
  loading.value   = false
}

function openCreate() {
  editingQuestion.value = null
  showModal.value = true
}

function openEdit(q) {
  editingQuestion.value = { ...q }
  showModal.value = true
}

async function saveQuestion(payload) {
  try {
    if (payload.id) {
      const { data } = await http.put(`/admin/questions/${payload.id}`, payload)
      const idx = questions.value.findIndex(q => q.id === payload.id)
      if (idx !== -1) questions.value[idx] = data
      toast.success('Soru güncellendi.')
    } else {
      const { data } = await http.post('/admin/questions', payload)
      questions.value.unshift(data)
      toast.success('Soru eklendi.')
    }
    showModal.value = false
  } catch (err) {
    toast.error(err.response?.data?.message || 'İşlem başarısız.')
  }
}

async function deleteQuestion(id) {
  if (!confirm('Bu soruyu silmek istediğinize emin misiniz?')) return
  try {
    await http.delete(`/admin/questions/${id}`)
    questions.value = questions.value.filter(q => q.id !== id)
    toast.success('Soru silindi.')
  } catch {
    toast.error('Silme işlemi başarısız.')
  }
}

function difficultyClass(d) {
  return d === 'EASY' ? 'bg-emerald-500/15 text-emerald-300'
    : d === 'MEDIUM' ? 'bg-yellow-500/15 text-yellow-300'
    : 'bg-red-500/15 text-red-300'
}
</script>
