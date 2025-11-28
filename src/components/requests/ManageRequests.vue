<script setup lang="ts">
import { computed } from "vue";
import ManageRequestsTable from "./ManageRequestsTable.vue";
import { useThemeStore } from "../../stores/themeStore";

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div
    class="w-full rounded-md p-5 transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div
      class="rounded-lg border p-6 shadow-sm transition-colors duration-300"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-300'"
    >
      <h2 class="font-bold text-4xl py-5">Pending Requests:</h2>
      <div class="flex items-center justify-between mb-3 gap-4">
        <div class="relative flex-1 max-w-md">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Search"
            class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none transition-colors w-full"
            :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white placeholder-gray-500' : 'bg-white border-gray-300 text-gray-800 placeholder-gray-400'"
          />
        </div>
      </div>

      <div
        class="overflow-x-auto rounded-md shadow-sm border transition-colors duration-300"
        :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
      >
        <ManageRequestsTable />
      </div>
    </div>
  </div>
</template>
