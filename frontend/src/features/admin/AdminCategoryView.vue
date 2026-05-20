<template>
  <div class="max-w-3xl mx-auto px-4 py-8">

    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-semibold text-white">Kategori Yönetimi</h1>
        <p class="text-gray-400 text-sm mt-0.5">Kategori oluştur, düzenle veya sil</p>
      </div>
      <button @click="openCreate" class="btn-primary text-sm">
        <font-awesome-icon icon="plus" class="mr-1.5" /> Yeni Kategori
      </button>
    </div>

    <!-- Liste -->
    <div class="card overflow-hidden p-0">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-white/10">
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Ad</th>
            <th class="px-5 py-3 text-left text-xs font-medium text-gray-400">Açıklama</th>
            <th class="px-5 py-3 text-right text-xs font-medium text-gray-400">İşlem</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="3" class="px-5 py-8 text-center text-gray-500">
              <font-awesome-icon icon="spinner" spin class="mr-2" /> Yükleniyor...
            </td>
          </tr>
          <tr v-else-if="!categories.length">
            <td colspan="3" class="px-5 py-8 text-center text-gray-500">Henüz kategori yok.</td>
          </tr>
          <tr
            v-for="cat in categories"
            :key="cat.id"
            class="border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors"
          >
            <td class="px-5 py-3.5 font-medium text-gray-200">{{ cat.name }}</td>
            <td class="px-5 py-3.5 text-gray-400">{{ cat.description || '—' }}</td>
            <td class="px-5 py-3.5 text-right">
              <button @click="openEdit(cat)" class="text-gray-400 hover:text-white mr-3 transition-colors">
                <font-awesome-icon icon="edit" />
              </button>
              <button @click="deleteCategory(cat.id)" class="text-gray-400 hover:text-red-400 transition-colors">
                <font-awesome-icon icon="trash" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4"
      @click.self="showModal = false"
    >
      <div class="bg-surface-800 border border-white/10 rounded-2xl w-full max-w-md p-6">
        <div class="flex items-center justify-between mb-5">
          <h2 class="font-semibold text-white">{{ editing ? 'Kategoriyi Düzenle' : 'Yeni Kategori' }}</h2>
          <button @click="showModal = false" class="text-gray-400 hover:text-white">
            <font-awesome-icon icon="times" />
          </button>
        </div>

        <form @submit.prevent="save" class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Ad</label>
            <input v-model="form.name" class="input" placeholder="Kategori adı" required />
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Açıklama</label>
            <input v-model="form.description" class="input" placeholder="Kısa açıklama (opsiyonel)" />
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="showModal = false" class="btn-secondary flex-1">İptal</button>
            <button type="submit" class="btn-primary flex-1">{{ editing ? 'Güncelle' : 'Kaydet' }}</button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import http from '@/api/http.js'

const toast      = useToast()
const loading    = ref(true)
const categories = ref([])
const showModal  = ref(false)
const editing    = ref(null)  // null = yeni, object = düzenleme

const form = ref({ name: '', description: '' })

onMounted(load)

async function load() {
  loading.value = true
  const { data } = await http.get('/categories')
  categories.value = data
  loading.value = false
}

function openCreate() {
  editing.value = null
  form.value = { name: '', description: '' }
  showModal.value = true
}

function openEdit(cat) {
  editing.value = cat
  form.value = { name: cat.name, description: cat.description || '' }
  showModal.value = true
}

async function save() {
  try {
    if (editing.value) {
      const { data } = await http.put(`/admin/categories/${editing.value.id}`, form.value)
      const idx = categories.value.findIndex(c => c.id === editing.value.id)
      if (idx !== -1) categories.value[idx] = data
      toast.success('Kategori güncellendi.')
    } else {
      const { data } = await http.post('/admin/categories', form.value)
      categories.value.push(data)
      toast.success('Kategori oluşturuldu.')
    }
    showModal.value = false
  } catch (err) {
    toast.error(err.response?.data?.message || 'İşlem başarısız.')
  }
}

async function deleteCategory(id) {
  if (!confirm('Bu kategoriyi silmek istediğinize emin misiniz?')) return
  try {
    await http.delete(`/admin/categories/${id}`)
    categories.value = categories.value.filter(c => c.id !== id)
    toast.success('Kategori silindi.')
  } catch (err) {
    toast.error(err.response?.data?.message || 'Silme işlemi başarısız.')
  }
}
</script>
