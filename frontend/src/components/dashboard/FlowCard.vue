<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useThemeStore } from "../../stores/themeStore";
import CreateNewFlow from "./CreateNewFlow.vue";
import type { Process } from "../../types";

interface Props {
  id?: string;
  title?: string;
  description?: string;
  processes?: Process[];
  isCreateNew?: boolean;
}

defineProps<Props>();

const router = useRouter();
const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const getProcessNames = (processes?: Process[]) => {
  if (!processes || processes.length === 0) return '';
  const names = processes.slice(0, 3).map((p) => p.name).join(', ');
  const suffix = processes.length > 3 ? ' ...' : '';
  return names + suffix;
};

const handleCardClick = (id?: string) => {
  if (id) {
    router.push(`/flow-definitions/${id}`);
  }
};
</script>

<template>
  <CreateNewFlow v-if="isCreateNew" />

  <div
    v-else
    @click="handleCardClick(id)"
    class="rounded-lg shadow p-6 hover:shadow-lg transition-shadow duration-200 min-h-[200px] w-60 flex flex-col cursor-pointer border"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-200 text-gray-900'"
  >
    <h3 class="font-bold text-xl mb-4 line-clamp-2">{{ title }}</h3>
    <div class="flex flex-col gap-2">
      <span :class="['text-sm', isDarkMode ? 'text-gray-300' : 'text-gray-400']">
        {{ description ?? 'No description available' }}
      </span>
      <span v-if="processes && processes.length > 0" :class="['text-xs', isDarkMode ? 'text-gray-400' : 'text-gray-500']">
        {{ getProcessNames(processes) }}
      </span>
    </div>
  </div>
</template>
