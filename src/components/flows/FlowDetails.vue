<script setup lang="ts">
import { computed } from "vue";
import type { FlowDefinition } from "../../types";
import FlowDiagram from "./FlowDiagram.vue";
import { useThemeStore } from "../../stores/themeStore";

const props = defineProps<{
  selectedDefinition: FlowDefinition | null;
}>();

const emit = defineEmits<{
  (e: "edit", payload: FlowDefinition | null): void;
  (e: "delete", payload: FlowDefinition | null): void;
}>();

const onEdit = () => emit("edit", props.selectedDefinition);
const onDelete = () => emit("delete", props.selectedDefinition);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
    <div
      v-for="(label, key) in {
        Name: selectedDefinition?.title,
        'Updated At': selectedDefinition?.updatedAt,
        Description: selectedDefinition?.description,
      }"
      :key="key"
      class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
    >
      <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
        {{ key }}
      </div>
      <div class="mt-1 text-lg font-semibold" :class="isDarkMode ? 'text-white' : 'text-gray-800'">
        {{ label || "no value" }}
      </div>
    </div>
  </div>
  <div class="mt-5">
    <p class="text-xs font-medium mb-3" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
      Diagram
    </p>
    <div
      v-if="
        selectedDefinition?.processes && selectedDefinition.processes.length > 0
      "
    >
      <FlowDiagram :processes="selectedDefinition.processes" />
    </div>
    <div
      v-else
      class="rounded-md p-4 border transition-colors duration-200 text-center"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-gray-300' : 'bg-gray-50 border-gray-200 text-gray-500'"
    >
      <p>No processes in this flow</p>
    </div>
  </div>
  <div class="flex items-center justify-center gap-3">
    <button
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5"
      type="button"
      @click="onEdit"
    >
      Edit
    </button>
    <button
      type="button"
      class="mt-5 py-2 px-4 border border-red-500 text-red-500 rounded-md hover:bg-red-50 transition-colors cursor-pointer"
      @click="onDelete"
    >
      Delete
    </button>
  </div>
</template>
