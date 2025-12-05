<script setup lang="ts">
import { computed } from "vue";
import { useThemeStore } from "../../stores/themeStore";
import CreateNewFlow from "./CreateNewFlow.vue";

interface Props {
  title?: string;
  description?: string;
  isCreateNew?: boolean;
}

defineProps<Props>();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <CreateNewFlow v-if="isCreateNew" />

  <div
    v-else
    class="rounded-lg shadow p-6 hover:shadow-lg transition-shadow duration-200 min-h-[200px] w-60 flex flex-col cursor-pointer border"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-200 text-gray-900'"
  >
    <h3 class="font-bold text-xl mb-4 line-clamp-2">{{ title }}</h3>
    <div class="flex flex-col items-center justify-center">
      <span :class="['text-sm', isDarkMode ? 'text-gray-300' : 'text-gray-400']">
        {{ description ?? 'No description available' }}
      </span>
    </div>
  </div>
</template>
