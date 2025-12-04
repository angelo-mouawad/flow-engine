<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import type { RequestSubmission } from "../types";
import RequestService from "../services/RequestService";
import { useThemeStore } from "../stores/themeStore";

import InfoBox from "../components/requests/details/InfoBox.vue";
import RequestActions from "../components/requests/details/RequestActions.vue";

const route = useRoute();
const router = useRouter();

const request = ref<RequestSubmission | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const fetchRequest = async (id: string) => {
  loading.value = true;
  error.value = null;
  try {
    request.value = await RequestService.getRequestById(id);
  } catch (err) {
    try {
      const allRequests = await RequestService.getRequests();
      const fallback = allRequests.find((req) => req.id === id);
      if (!fallback) {
        throw err;
      }
      request.value = fallback;
      return;
    } catch (fallbackErr) {
      const message =
        fallbackErr instanceof Error
          ? fallbackErr.message
          : "Failed to load request";
      error.value = message;
      request.value = null;
    }
  } finally {
    loading.value = false;
  }
};

const goBack = () => router.push({ name: "PendingRequests" });
const onActionCompleted = () => goBack();

onMounted(() => {
  if (typeof route.params.id === "string") fetchRequest(route.params.id);
});

watch(
  () => route.params.id,
  (newId) => {
    if (typeof newId === "string") fetchRequest(newId);
  }
);
</script>

<template>
  <div class="w-full rounded-md p-2 sm:p-5 transition-colors duration-300" :class="isDarkMode ? 'text-white' : 'text-gray-900'">
    <div class="rounded-lg border p-4 sm:p-6 shadow-sm transition-colors duration-300" :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-300'">
      
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between mb-6">
        <div>
          <p class="text-sm font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">Request</p>
          <h1 class="text-2xl font-semibold mt-1">{{ request?.requestType || "Request details" }}</h1>
          <p class="text-sm mt-1" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            Submitted by: <span class="font-semibold">{{ request?.data.allFields.submittedBy || "—" }}</span>
          </p>
        </div>
        <button type="button" class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 rounded-md shadow-sm transition-colors cursor-pointer self-start sm:self-auto" @click="goBack">
          Back to list
        </button>
      </div>

      <div v-if="loading" class="py-12 text-center">
        <p :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading request...</p>
      </div>

      <div v-else-if="error" class="rounded-md border px-5 py-4" :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'">
        <p class="font-semibold">Error</p>
        <p class="text-sm">{{ error }}</p>
      </div>

      <div v-else-if="!request" class="rounded-md border px-5 py-4 text-center" :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'">
        <p>No request data.</p>
      </div>

      <div v-else class="space-y-6">
        <InfoBox :request="request" />
        <RequestActions :request="request" :onActionCompleted="onActionCompleted" />
      </div>
    </div>
  </div>
</template>
