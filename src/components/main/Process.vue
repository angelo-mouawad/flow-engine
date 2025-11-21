<script setup lang="ts">
import { reactive, ref, watch, onMounted, computed } from "vue";
import type { Process } from "../../types";
import ProcessService from "../../services/ProcessService";

const props = defineProps<{ process?: Process }>(); // make it optional
const emit = defineEmits<{ (e: "update-process", payload: Partial<Process>): void }>();
const loading = ref(true);
const options = ref<string[]>([]);

const local = reactive({
  type: props.process?.processType ?? "",
});

const allProcesses = ref<Process[]>([]);

onMounted(async () => {
  allProcesses.value = await ProcessService.getProcess();
  loading.value = false;
  console.log("AllProcesses:", allProcesses.value);
  options.value = !props.process ? [] : allProcesses.value
    .filter(p => p.processType === props.process?.processType)
    .map(p => p.name);
});

watch(
  () => local.type,
  (val, oldVal) => {
    if (val !== oldVal) emit("update-process", { processType: val });
  }
);

watch(
  () => props.process?.processType,
  (newType) => {
    local.type = newType ?? "";
  }
);
</script>

<template>
  <div v-if="props.process && !loading">
    <label for="process-type" class="block text-gray-700 text-sm font-bold mb-2">
      {{ props.process.processType }} Type
    </label>
    <select
      id="process-type"
      v-model="local.type"
      class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
    >
      <option disabled>Select {{ props.process.processType }} type</option>
      <option v-for="opt in options" :key="opt" :value="opt">{{ opt }}</option>
    </select>
  </div>
  <div v-else-if="loading" class="text-gray-500">Loading process types...</div>
</template>