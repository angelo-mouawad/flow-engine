<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useThemeStore } from "../../stores/themeStore";

interface Props {
  title?: string;
  isCreateNew?: boolean;
}

defineProps<Props>();

const router = useRouter();
const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const handleCreateNew = () => {
  router.push("/flow-definitions/new");
};
</script>

<!-- too much repeated code, use wrappers? -->

<template>
  <div
    v-if="isCreateNew"
    @click="handleCreateNew"
    class="rounded-lg shadow p-6 flex flex-col items-center justify-center cursor-pointer hover:shadow-lg transition-shadow duration-200 min-h-[200px] w-60 border"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-200 text-gray-900'"
  >
    <div
      class="w-16 h-16 rounded-full border-4 border-yellow-400 flex items-center justify-center mb-4 flex-shrink-0"
    >
      <svg
        class="w-8 h-8 text-yellow-400"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="3"
          d="M12 4v16m8-8H4"
        />
      </svg>
    </div>
    <span class="text-yellow-500 font-bold text-xl text-center"
      >Create New Flow</span
    >
  </div>

  <div
    v-else
    class="rounded-lg shadow p-6 hover:shadow-lg transition-shadow duration-200 min-h-[200px] w-60 flex flex-col cursor-pointer border"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-200 text-gray-900'"
  >
    <h3 class="font-bold text-xl mb-4 line-clamp-2">{{ title }}</h3>
    <div class="flex flex-col items-center justify-center">
      <span :class="['text-sm', isDarkMode ? 'text-gray-300' : 'text-gray-400']">
        Flow diagram placeholder
      </span>
    </div>
  </div>
</template>
