<template>
  <div
    class="card flex flex-col items-center text-center gap-2 transition-colors duration-150"
    :class="{ 'border-primary-500/30 bg-primary-500/5': entry.username === currentUser }"
  >
    <!-- Madalya -->
    <div class="w-10 h-10 rounded-full flex items-center justify-center text-lg"
         :class="medalBg">
      <font-awesome-icon :icon="place === '1' ? 'trophy' : 'medal'" :class="medalColor" />
    </div>

    <!-- Sıra numarası -->
    <span class="text-xs font-bold text-gray-500">#{{ place }}</span>

    <!-- Kullanıcı adı -->
    <p class="font-semibold text-white text-sm leading-tight">
      {{ entry.username }}
      <span v-if="entry.username === currentUser"
        class="block text-[10px] text-primary-400 font-normal">Sen</span>
    </p>

    <!-- Skor -->
    <p class="text-xl font-bold" :class="scoreColor">{{ Math.round(entry.avgScore) }}%</p>

    <!-- Deneme sayısı -->
    <p class="text-[10px] text-gray-600">{{ entry.totalAttempts }} deneme</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  entry:       Object,
  currentUser: String,
  place:       String   // '1' | '2' | '3'
})

const medalBg = computed(() => ({
  '1': 'bg-yellow-500/15',
  '2': 'bg-gray-400/10',
  '3': 'bg-amber-600/15'
}[props.place]))

const medalColor = computed(() => ({
  '1': 'text-yellow-400',
  '2': 'text-gray-300',
  '3': 'text-amber-600'
}[props.place]))

const scoreColor = computed(() => {
  const s = props.entry?.avgScore ?? 0
  return s >= 80 ? 'text-emerald-400' : s >= 50 ? 'text-yellow-400' : 'text-red-400'
})
</script>
