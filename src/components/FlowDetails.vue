<script setup lang="ts">
import FlowDefinitionsTable from "./FlowDefinitionsTable.vue";
import FlowInstancesTable from "./FlowInstancesTable.vue";

const props = defineProps(["isDefinition"]);
</script>

<template>
  <div class="w-full rounded-md p-5">
    <div class="rounded-lg border bg-white p-6 border-gray-300 shadow-sm">
      <h2 class="font-bold text-4xl py-5">
        {{ isDefinition ? "Flow Definitions: " : "Flow Instances: " }}
      </h2>

      <div class="flex items-center justify-between mb-3 gap-4">
        <div class="relative flex-1 max-w-md">
          <svg
            class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
          <input
            type="text"
            placeholder="Search"
            class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none"
          />
        </div>

        <RouterLink v-if="isDefinition" to="/flow-definitions/new">
          <button
            class="rounded-md px-4 py-1 bg-yellow-500 text-sm font-medium cursor-pointer hover:bg-yellow-600 flex items-center gap-2"
          >
            <svg
              class="w-4 h-4 text-gray-800"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 4v16m8-8H4"
              />
            </svg>
            <span class="text-gray-800">New flow</span>
          </button>
        </RouterLink>
      </div>

      <div class="overflow-x-auto rounded-md shadow-sm border">
        <FlowDefinitionsTable v-if="isDefinition" />
        <FlowInstancesTable v-if="!isDefinition" />
      </div>
      <div class="mt-2 text-sm text-gray-500">
        <!-- Total: {{ definitions.length }} -->
      </div>
    </div>
  </div>
</template>
