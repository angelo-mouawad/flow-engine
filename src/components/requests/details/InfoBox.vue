<script setup lang="ts">
import { computed } from "vue";
import type { RequestSubmission, RequestType } from "../../../types";
import { useThemeStore } from "../../../stores/themeStore";

const props = defineProps<{
  request: RequestSubmission;
}>();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

type FieldConfig = {
  key: string;
  label?: string;
};

const requestTypeFieldConfig: Partial<Record<RequestType, FieldConfig[]>> = {
  ABSENCE_REQUEST: [
    { key: "submittedBy", label: "Submitted By" },
    { key: "startDate", label: "Start Date" },
    { key: "endDate", label: "End Date" },
    { key: "reason", label: "Reason" },
  ],
  CLOCKIN_REQUEST: [
    { key: "submittedBy", label: "Submitted By" },
    { key: "date", label: "Date" },
    { key: "startTime", label: "Start Time" },
    { key: "endTime", label: "End Time" },
  ],
};

const formatLabel = (key: string) =>
  key
    .replace(/([A-Z])/g, " $1")
    .replace(/_/g, " ")
    .replace(/^./, (char) => char.toUpperCase())
    .trim();

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

const displayedFields = computed(() => {
  const req = props.request;
  if (!req) return [];
  const allFields = req.data?.allFields ?? {};
  const config = requestTypeFieldConfig[req.requestTypeName];

  const rows: { key: string; label: string; value: string }[] = [];
  const seen = new Set<string>();

  const addRow = (key: string, label?: string) => {
    if (!(key in allFields)) return;
    seen.add(key);
    rows.push({
      key,
      label: label ?? formatLabel(key),
      value: formatFieldValue(allFields[key]),
    });
  };

  if (config) {
    config.forEach((field) => addRow(field.key, field.label));
  }

  Object.keys(allFields)
    .filter((key) => !seen.has(key))
    .forEach((key) => addRow(key));

  return rows;
});
</script>

<template>
  <div class="rounded-md border transition-colors duration-200 overflow-hidden"
    :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31] text-white' : 'bg-gray-50 border-gray-200 text-gray-900'">
    <table class="w-full text-sm">
      <tbody>
        <tr v-for="field in displayedFields" :key="field.key" class="border-b last:border-b-0"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'">
          <th
            class="px-4 py-3 text-left w-1/3 font-semibold"
            :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
          >
            {{ field.label }}
          </th>
          <td class="px-4 py-3">
            {{ field.value }}
          </td>
        </tr>
        <tr v-if="displayedFields.length === 0">
          <td colspan="2" class="px-4 py-3 text-center" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">
            No additional data available.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
