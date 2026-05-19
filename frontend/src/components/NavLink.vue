<template>
  <RouterLink
    :to="to"
    class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm text-gray-400 hover:text-white hover:bg-white/5 transition-colors duration-150"
    active-class="text-white bg-white/10"
  >
    <font-awesome-icon :icon="icon" class="text-xs" />
    <slot />
  </RouterLink>
</template>

<script setup>
import { RouterLink } from 'vue-router'
defineProps({ to: String, icon: String })
</script>
