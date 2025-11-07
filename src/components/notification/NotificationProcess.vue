<script setup lang="ts">
import { reactive, watch } from "vue";
import type { Process } from "../../types";

const props = defineProps<{
  step: Process;
}>();

const emit = defineEmits<{
  (e: "update-step", payload: Partial<Process>): void;
}>();

const local = reactive({
  title: props.step?.title ?? "",
  type: props.step?.type ?? "",
});

watch(
  () => local.title,
  (val) => {
    emit("update-step", { title: val });
  }
);
watch(
  () => local.type,
  (val) => {
    emit("update-step", { type: val });
  }
);

watch(
  () => props.step,
  (newStep) => {
    if (!newStep) return;
    local.title = newStep.title ?? "";
    local.type = newStep.type ?? "";
  },
  { deep: true }
);
</script>

<template>
  <div class="space-y-4">
    <!-- Process Name -->
    <div>
      <label class="block text-gray-700 text-sm font-bold mb-2">
        Notification Name
      </label>
      <input
        v-model="local.title"
        type="text"
        placeholder="e.g., Send Email Notification"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline"
      />
    </div>

    <!-- Process Type -->
    <div>
      <label class="block text-gray-700 text-sm font-bold mb-2">
        Notification Type
      </label>
      <select
        v-model="local.type"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
      >
        <option value="">Select notification type</option>
        <option value="email">Email</option>
        <option value="sms">SMS</option>
        <option value="popup">Popup</option>
      </select>
    </div>
  </div>
</template>
