<script setup lang="ts">
import { computed } from "vue";
import type { RequestSubmission } from "../../../types";
import { useThemeStore } from "../../../stores/themeStore";

defineProps<{ 
    request: RequestSubmission; 
}>();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const tryFormatDate = (value: string | Date) => {
  try {
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) {
      return null;
    }
    return date.toLocaleString();
  } catch {
    return null;
  }
};

const formatFieldValue = (value: unknown) => {
  if (value === null || value === undefined || value === "") return "—";
  if (value instanceof Date) return value.toLocaleString();
  if (typeof value === "boolean") return value ? "Yes" : "No";
  if (typeof value === "number") return value.toString();
  if (Array.isArray(value)) return value.join(", ");
  if (typeof value === "object") return JSON.stringify(value, null, 2);
  if (typeof value === "string") {
    const formatted = tryFormatDate(value);
    return formatted ?? value;
  }
  return String(value);
};

</script>

<template>
  <div class="rounded-md border transition-colors duration-200 overflow-hidden"
    :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31] text-white' : 'bg-gray-50 border-gray-200 text-gray-900'">
    <table class="w-full text-sm">
      <tbody>
        <tr v-for="(value, key) in request.data.allFields" :key="key" class="border-b last:border-b-0"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'">
          <th
            class="px-4 py-3 text-left w-1/3 font-semibold"
            :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
          >
              {{ (key as string).replace("_", " ") }}
          </th>
          <td class="px-4 py-3">
              {{ formatFieldValue(value) }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
