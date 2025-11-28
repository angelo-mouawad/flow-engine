<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import FlowInstanceService from "../services/FlowInstanceService";
import type { FlowInstance } from "../types";
import { useThemeStore } from "../stores/themeStore";

const route = useRoute();
const router = useRouter();

const flowInstance = ref<FlowInstance | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const statusBadgeClasses = computed(() => {
  const status = flowInstance.value?.flowStatus?.toLowerCase();
  const map: Record<string, string> = {
    active: "bg-emerald-100 text-emerald-800",
    success: "bg-lime-100 text-lime-800",
    pending: "bg-amber-100 text-amber-800",
    failed: "bg-red-100 text-red-800",
    failure: "bg-red-100 text-red-800",
    paused: "bg-blue-100 text-blue-800",
  };
  return map[status ?? ""] || "bg-gray-200 text-gray-800";
});

const fetchFlowInstance = async (id: string) => {
  loading.value = true;
  error.value = null;
  try {
    flowInstance.value = await FlowInstanceService.getFlowInstanceById(id);
  } catch (err) {
    // Fallback to fetching all instances if the direct endpoint is unavailable.
    try {
      const instances = await FlowInstanceService.getFlowInstances();
      const fallback = instances.find((inst) => inst.id === id);
      if (!fallback) {
        throw err;
      }
      flowInstance.value = fallback;
      return;
    } catch (fallbackErr) {
      const message =
        fallbackErr instanceof Error
          ? fallbackErr.message
          : "Failed to load flow instance";
      error.value = message;
      flowInstance.value = null;
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  if (typeof route.params.id === "string") {
    fetchFlowInstance(route.params.id);
  }
});

watch(
  () => route.params.id,
  (newId) => {
    if (typeof newId === "string") {
      fetchFlowInstance(newId);
    }
  }
);

const goBack = () => {
  router.push({ name: "FlowInstances" });
};

const formatDate = (dateValue?: string | Date) => {
  if (!dateValue) return "—";
  try {
    return new Date(dateValue).toLocaleString("en-GB", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return String(dateValue);
  }
};
</script>

<template>
  <div
    class="w-full rounded-md p-2 sm:p-5 transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div
      class="rounded-lg border p-4 sm:p-6 shadow-sm transition-colors duration-300"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-300'"
    >
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between mb-6">
        <div>
          <p class="text-sm font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            Flow Instance
          </p>
          <h1 class="text-2xl font-semibold mt-1">
            {{ flowInstance?.title || "Flow instance details" }}
          </h1>
        </div>
        <div class="flex flex-col sm:flex-row gap-3 sm:items-center">
          <span
            v-if="flowInstance"
            class="inline-flex items-center justify-center rounded-full px-4 py-1 text-sm font-semibold capitalize"
            :class="statusBadgeClasses"
          >
            {{ flowInstance.flowStatus.toLowerCase() }}
          </span>
          <button
            type="button"
            class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 rounded-md shadow-sm transition-colors cursor-pointer"
            @click="goBack"
          >
            Back to list
          </button>
        </div>
      </div>

      <div v-if="loading" class="py-12 text-center">
        <p :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading flow instance...</p>
      </div>

      <div
        v-else-if="error"
        class="rounded-md border px-5 py-4"
        :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'"
      >
        <p class="font-semibold">Error</p>
        <p class="text-sm">{{ error }}</p>
      </div>

      <div
        v-else-if="!flowInstance"
        class="rounded-md border px-5 py-4 text-center"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
      >
        <p>No flow instance data.</p>
      </div>

      <div v-else class="space-y-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div
            class="rounded-md p-4 border transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
          >
            <p class="text-xs font-medium uppercase tracking-wide" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
              Flow Definition
            </p>
            <p class="text-lg font-semibold mt-1">
              {{ flowInstance.flowDefinition?.title || "—" }}
            </p>
            <p class="text-sm mt-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
              {{ flowInstance.flowDefinition?.description || "No description provided." }}
            </p>
          </div>

          <div
            class="rounded-md p-4 border transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
          >
            <p class="text-xs font-medium uppercase tracking-wide" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
              Last updated
            </p>
            <p class="text-lg font-semibold mt-1">{{ formatDate(flowInstance.updatedAt) }}</p>
            <p class="text-xs mt-2" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
              Current process: <span class="font-semibold">{{ flowInstance.currentProcess?.name || "—" }}</span>
            </p>
          </div>
        </div>

        <div>
          <p class="text-xs font-medium uppercase tracking-wide mb-3" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            Process flow
          </p>

          <div
            v-if="flowInstance.flowDefinition?.processes?.length"
            class="rounded-md border p-4 transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
          >
            <ol class="relative border-l pl-6 ml-4" :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'">
              <li
                v-for="process in flowInstance.flowDefinition.processes"
                :key="process.id"
                class="mb-6 last:mb-0"
              >
                <span
                  class="absolute -left-3 flex items-center justify-center w-6 h-6 rounded-full border text-xs font-semibold"
                  :class="[
                    process.id === flowInstance.currentProcess?.id
                      ? 'bg-yellow-500 text-gray-900 border-yellow-500'
                      : isDarkMode
                        ? 'bg-[#1c1e1f] text-gray-300 border-[#2c2f31]'
                        : 'bg-white text-gray-600 border-gray-300'
                  ]"
                >
                  {{ process.processType?.charAt(0) || "•" }}
                </span>
                <div class="ml-2">
                  <p class="font-semibold" :class="process.id === flowInstance.currentProcess?.id ? 'text-yellow-500' : ''">
                    {{ process.name }}
                  </p>
                  <p class="text-xs uppercase tracking-wide" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
                    {{ process.processType }}
                  </p>
                </div>
              </li>
            </ol>
          </div>
          <div
            v-else
            class="rounded-md border p-4 text-center transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31] text-gray-300' : 'bg-gray-50 border-gray-200 text-gray-500'"
          >
            No processes defined for this flow.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
