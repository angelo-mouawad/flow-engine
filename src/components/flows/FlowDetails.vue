<script setup lang="ts">
import type { FlowDefinition } from '../../types';

defineProps<{
    selectedDefinition: FlowDefinition | null;
}>()
</script>

<template>
  <h2 class="text-2xl font-semibold mb-4 text-center">Flow Details</h2>

    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="(label, key) in {
        'Name': selectedDefinition?.title,
        'Updated At': selectedDefinition?.updatedAt,
        'Description': selectedDefinition?.description
      }" :key="key" class="bg-gray-50 border border-gray-200 rounded-md p-4">
        <div class="text-xs font-medium text-gray-500">{{ key }}</div>
        <div class="mt-1 text-lg font-semibold text-gray-800">{{ label || 'no value' }}</div>
      </div>
    </div>
      <div class="mt-5">
        <div v-for="(label, key) in {
            'Diagram': selectedDefinition?.title
          }" :key="key" class="bg-gray-50 border border-gray-200 rounded-md p-4">
          <div class="text-xs font-medium text-gray-500">{{ key }}</div>
          <div class="mt-1 text-lg font-semibold text-gray-800">{{ label || 'no value' }}</div>
        </div>
      </div>
      <div class="text-center">
        <button type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5">
          Edit
        </button>
      </div>
</template>