<script setup lang="ts">
import { computed } from "vue";
import { useThemeStore } from "../../stores/themeStore";

interface Props {
  label: string;
  color: string;
  value: string;
}

defineProps<Props>();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div
    class="rounded-lg shadow p-10 max-w-[250px] border transition-colors duration-200"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-200 text-gray-900'"
  >
    <div class="text-sm font-medium mb-2" :class="isDarkMode ? 'text-gray-200' : 'text-gray-700'">
      {{ label }}
    </div>
    <div class="text-3xl font-bold" :style="{ color: color }">
      #{{ value }}
    </div>
  </div>
</template>
