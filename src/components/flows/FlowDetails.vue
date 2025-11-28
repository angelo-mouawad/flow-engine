<script setup lang="ts">
import { ref, watch } from "vue";
import type { FlowDefinition } from "../../types";
import FlowDiagram from "./FlowDiagram.vue";
import FlowDefinitionService from "../../services/FlowDefinitionService";
const isEditing = ref(false);
const nameInput = ref("");
const descriptionInput = ref("");

const props = defineProps<{
  selectedDefinition: FlowDefinition | null;
}>();

const emit = defineEmits<{
  (e: "updated", payload: FlowDefinition): void;
}>();

// initialize inputs whenever the selected definition changes
watch(
  () => props.selectedDefinition,
  (newDef) => {
    nameInput.value = newDef?.title ?? "";
    descriptionInput.value = newDef?.description ?? "";
  },
  { immediate: true }
);

function startEdit() {
  if (!props.selectedDefinition) return;
  nameInput.value = props.selectedDefinition.title ?? "";
  descriptionInput.value = props.selectedDefinition.description ?? "";
  isEditing.value = true;
}

async function saveChanges() {
  if (!props.selectedDefinition) return;

  const updated: FlowDefinition = {
    ...props.selectedDefinition,
    title: nameInput.value,
    description: descriptionInput.value,
  };

  try {
    if (props.selectedDefinition.id === undefined) {
      throw new Error("Selected definition has no ID");
    }
    const result = await FlowDefinitionService.updateFlowDefinition(
      props.selectedDefinition.id,
      updated
    );
    emit("updated", result);
    isEditing.value = false;
  } catch (err) {
    console.error("Failed to save flow definition:", err);
  }
}

</script>

<template>
  <h2 class="text-2xl font-semibold mb-4 text-center">Flow Details</h2>
  <div v-if="!isEditing" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
    <div
      v-for="(label, key) in {
        Name: props.selectedDefinition?.title,
        'Updated At': props.selectedDefinition?.updatedAt,
        Description: props.selectedDefinition?.description,
      }"
      :key="key"
      class="bg-gray-50 border border-gray-200 rounded-md p-4"
    >
      <div class="text-l font-semibold text-gray-500">{{ key }}</div>
      <div class="mt-1 text-lg font-semibold text-gray-800">
        {{ label || "no value" }}
      </div>
    </div>
  </div>
  <div v-if="isEditing" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
    <div
      v-for="(label, key) in {
        Name: props.selectedDefinition?.title,
        'Updated At': props.selectedDefinition?.updatedAt,
        Description: props.selectedDefinition?.description,
      }"
      :key="key"
      class="bg-gray-50 border border-gray-200 rounded-md p-4"
    >
      <div class="text-l font-semibold text-gray-500">{{ key }}</div>
      <div class="mt-1">
        <template v-if="key === 'Name'">
          <input
            v-model="nameInput"
            class="w-full border border-gray-300 rounded-md p-2"
            type="text"
          />
        </template>
        <template v-else-if="key === 'Description'">
          <input
            v-model="descriptionInput"
            class="w-full border border-gray-300 rounded-md p-2"
            type="text"
          />
        </template>
        <template v-else>
          <div class="text-lg font-semibold text-gray-800">{{ label || 'no value' }}</div>
        </template>
      </div>
    </div>
  </div>
  <div class="mt-5">
    <p class="text-xs font-medium text-gray-500 mb-3">Diagram</p>
    <div v-if="props.selectedDefinition?.processes && props.selectedDefinition.processes.length > 0">
      <FlowDiagram :processes="props.selectedDefinition.processes" />
    </div>
    <div v-else class="bg-gray-50 border border-gray-200 rounded-md p-4">
      <p class="text-gray-500 text-center">No processes in this flow</p>
    </div>
  </div>
  <div class="text-center">
    <button
      v-if="!isEditing"
      @click="startEdit"
      type="button"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5"
    >
      Edit
    </button>
    <button
      v-if="isEditing"
      @click="saveChanges"
      type="button"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5"
    >
      Save
    </button>
  </div>
</template>
