<script setup lang="ts">
import ManageRequestsTable from './ManageRequestsTable.vue';
</script>

<template>
  <div class="w-full rounded-md p-2 sm:p-5">
    <div class="rounded-lg border bg-white p-2 sm:p-6 border-gray-300 shadow-sm">
      <h2 class="font-bold text-2xl sm:text-4xl py-3 sm:py-5">
        Pending Requests:
      </h2>

      <div class="flex flex-col sm:flex-row items-stretch sm:items-center justify-between mb-3 gap-2 sm:gap-4">
        <div class="relative flex-1 w-full max-w-md">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor"
            viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input type="text" placeholder="Search" class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none" />
        </div>
      </div>
      <div class="overflow-x-auto rounded-md shadow-sm border mt-2">
        <ManageRequestsTable />
      </div>
      <div class="mt-2 text-sm text-gray-500">
      </div>
    </div>
  </div>
</template>