<script setup lang="ts">
import type { FlowInstance } from "../../types";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import FlowInstanceService from "../../services/FlowInstanceService";
import { useThemeStore } from "../../stores/themeStore";

const loading = ref(false);
const flowInstances = ref<FlowInstance[]>([]);
const error = ref<string | null>(null);

const sortKey = ref<"title" | "status" | "flowDefinition" | "updatedAt">(
  "updatedAt"
);
const sortOrder = ref<"asc" | "desc">("asc");

const sortFlowInstances = (
  key: "title" | "status" | "flowDefinition" | "updatedAt"
) => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "asc";
  }
  flowInstances.value = [...flowInstances.value].sort((a, b) => {
    let result = 0;
    if (key === "title") {
      if (a.title.toLowerCase() < b.title.toLowerCase()) result = -1;
      if (a.title.toLowerCase() > b.title.toLowerCase()) result = 1;
    } else if (key === "status") {
      const order: Record<string, number> = {
        ACTIVE: 0,
        PENDING: 1,
        FAILED: 2,
      };
      result = (order[a.flowStatus] ?? 99) - (order[b.flowStatus] ?? 99);
    } else if (key === "flowDefinition") {
      if (
        a.flowDefinition.title.toLowerCase() <
        b.flowDefinition.title.toLowerCase()
      )
        result = -1;
      if (
        a.flowDefinition.title.toLowerCase() >
        b.flowDefinition.title.toLowerCase()
      )
        result = 1;
    } else if (key === "updatedAt") {
      result =
        new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime();
    }
    return sortOrder.value === "asc" ? result : -result;
  });
};

const fetchFlowInstances = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await FlowInstanceService.getFlowInstances();
    flowInstances.value = data;
    sortFlowInstances(sortKey.value);
  } catch (e) {
    error.value =
      e instanceof Error
        ? e.message
        : "An error occurred while fetching flow instances";
    console.error("Failed to fetch flow instances:", e);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchFlowInstances();
});

const router = useRouter();

const viewFlowInstance = (instanceId: string) => {
  router.push({ name: "FlowInstanceDetails", params: { id: instanceId } });
};

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <table
    class="w-full border-collapse text-sm transition-colors duration-200"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <thead>
      <tr class="text-left text-white bg-[#111]">
        <th
          class="px-4 py-2 font-medium select-none cursor-pointer"
          @click="sortFlowInstances('title')"
        >
          Name
          <span
            class="ml-2 inline-block"
            style="width: 1.5em; text-align: left"
          >
            <span v-if="sortKey === 'title'">
              <span v-if="sortOrder === 'asc'">▲</span>
              <span v-else>▼</span>
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
        <th
          class="px-4 py-2 font-medium select-none cursor-pointer"
          @click="sortFlowInstances('status')"
        >
          Status
          <span
            class="ml-2 inline-block"
            style="width: 1.5em; text-align: left"
          >
            <span v-if="sortKey === 'status'">
              <span v-if="sortOrder === 'asc'">▲</span>
              <span v-else>▼</span>
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
        <th
          class="px-4 py-2 font-medium select-none cursor-pointer"
          @click="sortFlowInstances('flowDefinition')"
        >
          Flow Definition
          <span
            class="ml-2 inline-block"
            style="width: 1.5em; text-align: left"
          >
            <span v-if="sortKey === 'flowDefinition'">
              <span v-if="sortOrder === 'asc'">▲</span>
              <span v-else>▼</span>
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
        <th
          class="px-4 py-2 font-medium select-none cursor-pointer"
          @click="sortFlowInstances('updatedAt')"
        >
          Last Updated
          <span
            class="ml-2 inline-block"
            style="width: 1.5em; text-align: left"
          >
            <span v-if="sortKey === 'updatedAt'">
              <span v-if="sortOrder === 'asc'">▲</span>
              <span v-else>▼</span>
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
      </tr>
    </thead>
    <tbody
      class="divide-y"
      :class="isDarkMode ? 'divide-[#2c2f31]' : 'divide-gray-200'"
    >
      <div v-if="loading" class="flex items-center justify-center py-8">
        <div :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
          Loading flow instances...
        </div>
      </div>

      <div
        v-else-if="error"
        class="px-4 py-3 rounded border"
        :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'"
      >
        <p class="font-medium">Error</p>
        <p class="text-sm">{{ error }}</p>
      </div>

      <div
        v-else-if="flowInstances.length === 0"
        class="text-center py-8"
        :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'"
      >
        No flow instances found.
      </div>
      <tr
        class="cursor-pointer transition-colors"
        :class="isDarkMode ? 'hover:bg-[#242628]' : 'hover:bg-gray-100'"
        v-for="inst in flowInstances"
        :key="inst.id"
        @click="viewFlowInstance(inst.id)"
      >
        <td class="px-4 py-2 font-medium">{{ inst.title }}</td>
        <td class="px-4 py-2">
          <div class="flex items-center gap-2">
            <span
              class="inline-block w-3 h-3 rounded-full flex-shrink-0"
              :class="{
                'bg-[#10b981]': inst.flowStatus.toLowerCase() === 'active',
                'bg-[#84cc16]': inst.flowStatus.toLowerCase() === 'success',
                'bg-[#f59e0b]': inst.flowStatus.toLowerCase() === 'pending',
                'bg-[#ef4444]': inst.flowStatus.toLowerCase() === 'failed',
                'bg-[#3b82f6]': inst.flowStatus.toLowerCase() === 'paused',
              }"
            ></span>
            <span :class="['font-medium', isDarkMode ? 'text-white' : 'text-gray-900']">{{
              inst.flowStatus.charAt(0).toUpperCase() +
              inst.flowStatus.slice(1).toLowerCase()
            }}</span>
          </div>
        </td>
        <td class="px-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
          {{ inst.flowDefinition.title }}
        </td>
        <td class="px-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
          {{
            new Date(inst.updatedAt).toLocaleDateString("en-GB", {
              year: "numeric",
              month: "long",
              day: "numeric",
            })
          }}
        </td>
      </tr>
    </tbody>
  </table>
</template>
