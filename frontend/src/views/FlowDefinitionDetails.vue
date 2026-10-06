<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import type { FlowDefinition } from "../types";
import FlowDefinitionService from "../services/FlowDefinitionService";
import FlowDetails from "../components/flows/FlowDetails.vue";
import { useThemeStore } from "../stores/themeStore";

const route = useRoute();
const router = useRouter();

const flowDefinition = ref<FlowDefinition | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const fetchFlowDefinition = async (id: string) => {
  loading.value = true;
  error.value = null;
  try {
    flowDefinition.value = await FlowDefinitionService.getFlowDefinitionById(id);
  } catch (err) {
    try {
      const definitions = await FlowDefinitionService.getFlowDefinitions();
      const fallback = definitions.find((def) => def.id === id);
      if (!fallback) {
        throw err;
      }
      flowDefinition.value = fallback;
      return;
    } catch (fallbackErr) {
      const message =
        fallbackErr instanceof Error
          ? fallbackErr.message
          : "Failed to load flow definition";
      error.value = message;
      flowDefinition.value = null;
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  if (typeof route.params.id === "string") {
    fetchFlowDefinition(route.params.id);
  }
});

watch(
  () => route.params.id,
  (newId) => {
    if (typeof newId === "string") {
      fetchFlowDefinition(newId);
    }
  }
);

const goBack = () => {
  router.push({ name: "FlowDefinitions" });
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
            Flow Definition
          </p>
          <h1 class="text-2xl font-semibold mt-1">
            {{ flowDefinition?.title || "Flow definition details" }}
          </h1>
        </div>
        <button
          type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 rounded-md shadow-sm transition-colors cursor-pointer"
          @click="goBack"
        >
          Back to list
        </button>
      </div>

      <div v-if="loading" class="py-12 text-center">
        <p :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading flow definition...</p>
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
        v-else-if="!flowDefinition"
        class="rounded-md border px-5 py-4 text-center"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
      >
        <p>No flow definition data.</p>
      </div>

      <div v-else class="space-y-8">
        <FlowDetails :selected-definition="flowDefinition" />
      </div>
    </div>
  </div>
</template>
