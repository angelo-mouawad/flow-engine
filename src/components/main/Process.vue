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
  type: props.step?.type ?? "",
});

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
    local.type = newStep.type ?? "";
  },
  { deep: true }
);
</script>

<template>
     <div>
    <div>
      <label for="step-type" class="block text-gray-700 text-sm font-bold mb-2">
        {{ step.type }} Type
      </label>
      <select
        id="step-type"
        v-model="local.type"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
      >
          <option disabled>Select {{ step.type }} type</option>
          <option v-if="step.type=='Notification'" value="email">Email</option>
          <option v-if="step.type=='Notification'" value="popup">Popup</option>
          <option v-if="step.type=='Request'"value="absence">Absence</option>
      </select>
    </div>

  </div>
</template>
