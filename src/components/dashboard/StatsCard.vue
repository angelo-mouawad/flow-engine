<script setup lang="ts">
  interface Props {
      label: string,
      color: string,
      value: string
  };

  defineProps<Props>();
</script>

<template>
  <div class="bg-white rounded-lg shadow p-10 max-w-[250px]">
    <div class="text-sm font-medium text-gray-700 mb-2">
      {{ label }}
    </div>
    <div
      class="text-3xl font-bold"
      :style="{ color: color }"
    >
      #{{ value }}
    </div>
  </div>
</template>