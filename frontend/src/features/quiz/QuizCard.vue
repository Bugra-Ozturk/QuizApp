<template>
  <div class="card hover:border-primary-500/30 transition-colors duration-150 flex flex-col gap-4">
    <!-- Kategori rozeti -->
    <div class="flex items-start justify-between">
      <span class="badge bg-primary-500/15 text-primary-300 text-xs">{{ quiz.categoryName }}</span>
      <span class="text-xs text-gray-500 flex items-center gap-1">
        <font-awesome-icon icon="clock" />
        {{ formatTime(quiz.timeLimitSeconds) }}
      </span>
    </div>

    <!-- Başlık -->
    <div class="flex-1">
      <h3 class="font-semibold text-white leading-snug">{{ quiz.title }}</h3>
      <p class="text-sm text-gray-400 mt-1">{{ quiz.questionCount }} soru</p>
    </div>

    <!-- Başla butonu -->
    <button @click="$emit('play', quiz.id)" class="btn-primary w-full text-sm">
      <font-awesome-icon icon="play" class="mr-1.5" />
      Başla
    </button>
  </div>
</template>

<script setup>
defineProps({ quiz: Object })
defineEmits(['play'])

function formatTime(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return s === 0 ? `${m} dk` : `${m}:${String(s).padStart(2,'0')}`
}
</script>
