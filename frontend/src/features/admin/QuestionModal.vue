<template>
  <!-- Overlay -->
  <div class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-surface-800 border border-white/10 rounded-2xl w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto">

      <div class="flex items-center justify-between mb-5">
        <h2 class="font-semibold text-white">{{ isEdit ? 'Soruyu Düzenle' : 'Yeni Soru' }}</h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-white">
          <font-awesome-icon icon="times" />
        </button>
      </div>

      <form @submit.prevent="submit" class="space-y-4">

        <!-- Soru metni -->
        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">Soru Metni</label>
          <textarea v-model="form.text" class="input resize-none" rows="3" placeholder="Soruyu yazın..." required />
        </div>

        <!-- Şıklar -->
        <div>
          <label class="block text-xs font-medium text-gray-400 mb-1.5">Seçenekler</label>
          <div class="space-y-2">
            <div v-for="i in 4" :key="i" class="flex items-center gap-2">
              <span class="text-xs font-medium text-gray-500 w-5">{{ String.fromCharCode(64 + i) }}.</span>
              <input
                v-model="form.options[i - 1]"
                class="input"
                :placeholder="`Seçenek ${i}`"
                required
              />
              <input
                type="radio"
                :value="i - 1"
                v-model="form.correctOptionIndex"
                class="accent-primary-500 w-4 h-4 flex-shrink-0"
                :title="`Doğru cevap: ${i}`"
              />
            </div>
          </div>
          <p class="text-xs text-gray-500 mt-1">Doğru cevabın yanındaki radyo butonu seçili olmalı.</p>
        </div>

        <!-- Kategori + Zorluk -->
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Kategori</label>
            <select v-model="form.categoryId" class="input" required>
              <option value="">Seçin...</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1.5">Zorluk</label>
            <select v-model="form.difficulty" class="input" required>
              <option value="EASY">Kolay</option>
              <option value="MEDIUM">Orta</option>
              <option value="HARD">Zor</option>
            </select>
          </div>
        </div>

        <div class="flex gap-3 pt-2">
          <button type="button" @click="$emit('close')" class="btn-secondary flex-1">İptal</button>
          <button type="submit" class="btn-primary flex-1">
            {{ isEdit ? 'Güncelle' : 'Kaydet' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ question: Object, categories: Array })
const emit  = defineEmits(['save', 'close'])

const isEdit = computed(() => !!props.question?.id)

const form = ref({
  id: props.question?.id ?? null,
  text: props.question?.text ?? '',
  options: props.question?.options?.slice() ?? ['', '', '', ''],
  correctOptionIndex: props.question?.correctOptionIndex ?? 0,
  categoryId: props.question?.categoryId ?? '',
  difficulty: props.question?.difficulty ?? 'MEDIUM'
})

function submit() {
  emit('save', { ...form.value })
}
</script>
