<script setup lang="ts">
  interface Props {
    title?: string,
    isCreateNew?: boolean
  };

  defineProps<Props>();
</script>

<template>
  <div v-if="isCreateNew" class="bg-white rounded-lg shadow p-6 flex flex-col items-center justify-center cursor-pointer hover:shadow-lg transition-shadow min-h-[200px] max-w-[250px]">
    <div class="w-16 h-16 rounded-full border-5 border-yellow-400 flex items-center justify-center mb-4">
      <span class="text-yellow-400 font-bold text-3xl">+</span>
    </div>
    <span class="text-yellow-500 font-bold text-xl">Create New Flow</span>
  </div>

  <div v-else class="bg-white rounded-lg shadow p-6 haver:shadow-lg transition-shadow min-h-[200px] max-w-[250px]">
    <h3 class="font-bold text-xl mb-4">{{ title }}</h3>
    <div class="flex flex-cols items-center">
      <span>Some Diagram</span>
    </div>
  </div>
</template>