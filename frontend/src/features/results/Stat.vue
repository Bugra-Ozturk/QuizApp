<template>
  <div class="text-center">
    <font-awesome-icon :icon="icon" class="text-lg mb-1" :class="color" />
    <p class="text-xl font-semibold text-white">{{ value }}</p>
    <p class="text-xs text-gray-500">{{ label }}</p>
  </div>
</template>

<script setup>
defineProps({ icon: String, color: String, value: [String, Number], label: String })
</script>
