<script setup lang="ts">
import { ref, computed } from "vue";
import type { RequestSubmission } from "../../../types";
import RequestService from "../../../services/RequestService";
import { useThemeStore } from "../../../stores/themeStore";

const props = defineProps<{
  request: RequestSubmission;
  onActionCompleted: () => void;
}>();

const actionLoading = ref(false);
const actionError = ref<string | null>(null);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const handleAction = async (type: "approve" | "decline") => {
  if (!props.request) return;
  actionLoading.value = true;
  actionError.value = null;
  try {
    if (type === "approve") {
      await RequestService.approveRequest(props.request.id);
    } else {
      await RequestService.declineRequest(props.request.id);
    }
    props.onActionCompleted();
  } catch (err) {
    actionError.value = err instanceof Error ? err.message : "Failed to update request";
  } finally {
    actionLoading.value = false;
  }
};
</script>

<template>
  <div>
    <div v-if="actionError" class="rounded-md border px-5 py-4 mb-4"
      :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'">
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
</template>
