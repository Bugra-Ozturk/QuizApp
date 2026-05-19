<template>
  <div class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-surface-800 border border-white/10 rounded-2xl w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto">

      <div class="flex items-center justify-between mb-5">
        <h2 class="font-semibold text-white">{{ isEdit ? 'Quizi Düzenle' : 'Yeni Quiz' }}</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-white">
          <font-awesome-icon icon="times" />
        </button>
      </div>

      <form @submit.prevent="submit" class="space-y-4">

        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">Başlık</label>
          <input v-model="form.title" class="input" placeholder="Quiz başlığı" required />
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Kategori</label>
            <select v-model="form.categoryId" class="input" required>
              <option value="">Seçin...</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Süre (saniye)</label>
            <input v-model.number="form.timeLimitSeconds" type="number" min="30" class="input" required />
          </div>
        </div>

        <!-- Soru seçimi -->
        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">
            Sorular ({{ form.questionIds.length }} seçili)
          </label>
          <div class="max-h-48 overflow-y-auto border border-white/10 rounded-lg divide-y divide-white/5">
            <label
              v-for="q in allQuestions"
              :key="q.id"
              class="flex items-center gap-3 px-4 py-2.5 hover:bg-white/5 cursor-pointer"
            >
              <input
                type="checkbox"
                :value="q.id"
                v-model="form.questionIds"
                class="accent-primary-500"
              />
              <span class="text-sm text-gray-300 truncate">{{ q.text }}</span>
              <span class="badge text-xs ml-auto flex-shrink-0 bg-white/5 text-gray-400">{{ q.categoryName }}</span>
            </label>
          </div>
        </div>

        <div class="flex gap-3 pt-2">
          <button type="button" @click="$emit('close')" class="btn-secondary flex-1">İptal</button>
          <button type="submit" class="btn-primary flex-1" :disabled="form.questionIds.length === 0">
            {{ isEdit ? 'Güncelle' : 'Kaydet' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ quiz: Object, categories: Array, allQuestions: Array })
const emit  = defineEmits(['save', 'close'])

const isEdit = computed(() => !!props.quiz?.id)

const form = ref({
  id:               props.quiz?.id ?? null,
  title:            props.quiz?.title ?? '',
  categoryId:       props.quiz?.categoryId ?? '',
  timeLimitSeconds: props.quiz?.timeLimitSeconds ?? 300,
  questionIds:      []
})

function submit() {
  emit('save', { ...form.value })
}
</script>
