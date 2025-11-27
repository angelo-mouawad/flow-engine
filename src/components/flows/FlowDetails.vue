<script setup lang="ts">
import { ref } from "vue";
import type { FlowDefinition } from "../../types";
import FlowDiagram from "./FlowDiagram.vue";
import FlowDefinitionService from "../../services/FlowDefinitionService";
const isEditing = ref(false);
const nameInput = ref("");
const descriptionInput = ref("");

defineProps<{
  selectedDefinition: FlowDefinition | null;
}>();

</script>

<template>
  <h2 class="text-2xl font-semibold mb-4 text-center">Flow Details</h2>
  <div v-if="!isEditing" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
    <div
      v-for="(label, key) in {
        Name: selectedDefinition?.title,
        'Updated At': selectedDefinition?.updatedAt,
        Description: selectedDefinition?.description,
      }"
      :key="key"
      class="bg-gray-50 border border-gray-200 rounded-md p-4"
    >
      <div class="text-l font-semibold text-gray-500">{{ key }}</div>
      <div class="mt-1 text-lg font-semibold text-gray-800">
        {{ label || "no value" }}
      </div>
    </div>
  </div>
  <div v-if="isEditing" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
    <div
      v-for="(label, key) in {
        Name: selectedDefinition?.title,
        'Updated At': selectedDefinition?.updatedAt,
        Description: selectedDefinition?.description,
      }"
      :key="key"
      class="bg-gray-50 border border-gray-200 rounded-md p-4"
    >
      <div class="text-l font-semibold text-gray-500">{{ key }}</div>
      <input
        class="mt-1 w-full border border-gray-300 rounded-md p-2"
        :value="label || 'no value'"
      />
    </div>
  </div>
  <div class="mt-5">
    <p class="text-xs font-medium text-gray-500 mb-3">Diagram</p>
    <div
      v-if="
        selectedDefinition?.processes && selectedDefinition.processes.length > 0
      "
    >
      <FlowDiagram :processes="selectedDefinition.processes" />
    </div>
    <div v-else class="bg-gray-50 border border-gray-200 rounded-md p-4">
      <p class="text-gray-500 text-center">No processes in this flow</p>
    </div>
  </div>
  <div class="text-center">
    <button
      v-if="!isEditing"
      @click="isEditing=true"
      type="button"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5"
    >
      Edit
    </button>
    <button
      v-if="isEditing"
      @click="isEditing=false"
      type="button"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5"
    >
      Save
    </button>
  </div>
</template>
