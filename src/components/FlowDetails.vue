<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import type { FlowDefinition } from "../types";

const props = defineProps(["isDefinition"]);

const definitions = ref<FlowDefinition[]>([]);
const searchText = ref<string>("");

// hardcoded as example
onMounted(() => {
  if (props.isDefinition) {
    definitions.value = [
      {
        id: "1",
        title: "Basic Absence Approval",
        description: "Employee submits absence → Manager approves",
        processes: [],
        createdAt: new Date("2025-01-15"),
      },
      {
        id: "2",
        title: "Multi-Level Absence Approval",
        description: "Employee submits → Team Lead approves → HR validates",
        processes: [],
        createdAt: new Date("2025-02-20"),
      },
    ];
  }
});

const filteredDefinitions = computed(() => {
  if (!searchText.value) {
    return definitions.value;
  }

  const search = searchText.value.toLowerCase();

  return definitions.value.filter(
    (def) =>
      def.title.toLowerCase().includes(search) ||
      def.description.toLowerCase().includes(search)
  );
});

// we can change to a different format
const formatDate = (date?: Date) => {
  if (!date) return "—";
  return new Date(date).toLocaleDateString("en-GB", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
};

const formatProcesses = (processes: any[]) => {
  if (processes && processes.length > 0) {
    return processes.length;
  }
  return "—";
};
</script>

<template>
  <div class="w-full rounded-md border p-5">
    <h1 class="font-extrabold text-4xl py-5">
      {{ isDefinition ? "Flow Definitions: " : "Flow Instances: " }}
    </h1>

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
          v-model="searchText"
          type="text"
          placeholder="Search"
          class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none"
        />
      </div>

      <RouterLink v-if="isDefinition" to="/flow-definitions/new">
        <button
          class="rounded-md px-4 py-1 bg-yellow-500 text-sm font-medium border cursor-pointer hover:bg-yellow-600 flex items-center gap-2"
        >
          <svg
            class="w-4 h-4"
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
          <span>New flow</span>
        </button>
      </RouterLink>
    </div>

    <div class="overflow-x-auto rounded-md shadow-sm border">
      <table class="w-full border-collapse text-sm">
        <thead>
          <tr class="text-left text-white bg-[#111]">
            <th class="px-4 py-2 font-medium">Name</th>
            <th class="px-4 py-2 font-medium">Description</th>
            <th class="px-4 py-2 font-medium">Process</th>
            <th class="px-4 py-2 font-medium">Created At</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr
            class="cursor-pointer hover:bg-gray-100"
            v-for="def in filteredDefinitions"
            :key="def.id"
          >
            <td class="px-4 py-2">{{ def.title }}</td>
            <td class="px-4 py-2">{{ def.description }}</td>
            <td class="px-4 py-2">{{ formatProcesses(def.processes) }}</td>
            <td class="px-4 py-2">{{ formatDate(def.createdAt) }}</td>
          </tr>

          <tr v-if="filteredDefinitions.length === 0">
            <td class="px-4 py-2 text-left text-gray-500">No flow found</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
