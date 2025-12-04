<script setup lang="ts">
import { computed } from "vue";
import type { RequestSubmission } from "../../../types";
import { useThemeStore } from "../../../stores/themeStore";

const props = defineProps<{
  request: RequestSubmission;
}>();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

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
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div class="rounded-md p-4 border transition-colors duration-200"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-gray-50 border-gray-200 text-gray-900'">
        <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">Start Date</div>
        <div class="mt-1 text-lg font-semibold">{{ formatDate(props.request.data.allFields.startDate) }}</div>
      </div>

      <div class="rounded-md p-4 border transition-colors duration-200"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-gray-50 border-gray-200 text-gray-900'">
        <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">End Date</div>
        <div class="mt-1 text-lg font-semibold">{{ formatDate(props.request.data.allFields.endDate) }}</div>
      </div>
    </div>

    <div class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-gray-50 border-gray-200 text-gray-900'">
      <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">Reason</div>
      <div class="mt-1 text-lg font-semibold">{{ props.request.data.allFields.reason || "No value" }}</div>
    </div>
  </div>
</template>
