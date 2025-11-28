<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import type { RequestSubmission } from "../types";
import RequestService from "../services/RequestService";
import { useThemeStore } from "../stores/themeStore";

const route = useRoute();
const router = useRouter();

const request = ref<RequestSubmission | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);
const actionError = ref<string | null>(null);
const actionLoading = ref(false);

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

const goBack = () => {
  router.push({ name: "ManageRequests" });
};

const handleAction = async (type: "approve" | "decline") => {
  if (!request.value) return;
  actionLoading.value = true;
  actionError.value = null;
  try {
    if (type === "approve") {
      await RequestService.approveRequest(request.value.id);
    } else {
      await RequestService.declineRequest(request.value.id);
    }
    goBack();
  } catch (err) {
    actionError.value =
      err instanceof Error ? err.message : "Failed to update request";
  } finally {
    actionLoading.value = false;
  }
};

const formatDate = (dateValue?: Date | string) => {
  if (!dateValue) return "—";
  try {
    return new Date(dateValue).toLocaleDateString("en-GB", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  } catch {
    return String(dateValue);
  }
};

onMounted(() => {
  if (typeof route.params.id === "string") {
    fetchRequest(route.params.id);
  }
});

watch(
  () => route.params.id,
  (newId) => {
    if (typeof newId === "string") {
      fetchRequest(newId);
    }
  }
);
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
            Request
          </p>
          <h1 class="text-2xl font-semibold mt-1">
            {{ request?.requestType || "Request details" }}
          </h1>
          <p class="text-sm mt-1" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            Submitted by:
            <span class="font-semibold">{{ request?.data.allFields.submittedBy || "—" }}</span>
          </p>
        </div>
        <button
          type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 rounded-md shadow-sm transition-colors cursor-pointer self-start sm:self-auto"
          @click="goBack"
        >
          Back to list
        </button>
      </div>

      <div v-if="loading" class="py-12 text-center">
        <p :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading request...</p>
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
        v-else-if="!request"
        class="rounded-md border px-5 py-4 text-center"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
      >
        <p>No request data.</p>
      </div>

      <div v-else class="space-y-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div
            class="rounded-md p-4 border transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
          >
            <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
              Start Date
            </div>
            <div class="mt-1 text-lg font-semibold">
              {{ formatDate(request.data.allFields.startDate) }}
            </div>
          </div>
          <div
            class="rounded-md p-4 border transition-colors duration-200"
            :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
          >
            <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
              End Date
            </div>
            <div class="mt-1 text-lg font-semibold">
              {{ formatDate(request.data.allFields.endDate) }}
            </div>
          </div>
        </div>

        <div
          class="rounded-md p-4 border transition-colors duration-200"
          :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
        >
          <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            Reason
          </div>
          <div class="mt-1 text-lg font-semibold">
            {{ request.data.allFields.reason || "No value" }}
          </div>
        </div>

        <div
          v-if="actionError"
          class="rounded-md border px-5 py-4"
          :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'"
        >
          <p class="font-semibold">Action failed</p>
          <p class="text-sm">{{ actionError }}</p>
        </div>

        <div class="flex flex-col sm:flex-row gap-4 sm:justify-center">
          <button
            type="button"
            class="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
            :disabled="actionLoading"
            @click="handleAction('approve')"
          >
            Approve
          </button>
          <button
            type="button"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
            :disabled="actionLoading"
            @click="handleAction('decline')"
          >
            Decline
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
