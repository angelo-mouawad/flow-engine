<script setup lang="ts">
import { ref, watch, computed } from "vue";
import type { FlowDefinition } from "../../types";
import FlowDiagram from "./FlowDiagram.vue";
import FlowDefinitionService from "../../services/FlowDefinitionService";
const isEditing = ref(false);
const nameInput = ref("");
const descriptionInput = ref("");
import { useThemeStore } from "../../stores/themeStore";

const props = defineProps<{
  selectedDefinition: FlowDefinition | null;
}>();

const emit = defineEmits<{
  (e: "updated", payload: FlowDefinition): void;
}>();

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
import { useRouter } from "vue-router";

const router = useRouter();

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
    router.push("/flow-definitions");
  } catch (err) {
    console.error("Failed to save flow definition:", err);
  }
}

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const formatDate = (dateValue?: string | Date) => {
  if (!dateValue) return "—";
  try {
    return new Date(dateValue).toLocaleString("en-GB", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return String(dateValue);
  }
};
</script>

<template>
  <div v-if="!isEditing" class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div
      class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
    >
      <p
        class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'"
      >
        name
      </p>
      <p class="text-lg font-semibold mt-1">
        {{ props.selectedDefinition?.title || "—" }}
      </p>
    </div>

    <div
      class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
    >
      <p
        class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'"
      >
        last updated
      </p>
      <p class="text-lg font-semibold mt-1">{{ formatDate(props.selectedDefinition?.updatedAt) }}</p>
    </div>
    <div
      class="rounded-md p-4 border transition-colors duration-200 col-span-full"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
    >
      <p
        class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode? 'text-gray-400' : 'text-gray-500'"
      >
        description
      </p>
      <p class="text-lg font-semibold mt-1">
        {{ props.selectedDefinition?.description }}
      </p>
    </div>
  </div>
  <div v-if="isEditing" class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div
      class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2d31]' : 'bg-gray-50 border-gray-200'"
    >
      <p class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'"
      >
        name
      </p>
      <input
        v-model="nameInput"
        type="text"
        class="w-full mt-2 border rounded-md p-2 outline-none transition-colors focus:border-sidebarprimary focus:ring-sidebarprimary"
        :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2d31]' : 'bg-gray-50 border-gray-200'"
      />
    </div>
    <div
      class="rounded-md p-4 border transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2d31]' : 'bg-gray-50 border-gray-200'"
    >
      <p 
        class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'"
      >
        last updated
      </p>
      <p class="text-lg font-semibold mt-2 opacity-70">
        {{ formatDate(props.selectedDefinition?.updatedAt) }}
      </p>
    </div>
    <div
      class="rounded-md p-4 border transition-colors duration-200 col-span-full"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2d31]' : 'bg-gray-50 border-gray-200'"
    >
      <p 
        class="text-xs font-medium uppercase tracking-wide"
        :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'"
      >
        description
      </p>
      <textarea
        v-model="descriptionInput"
        rows="3"
        class="w-full mt-2 border rounded-md outline-none p-2 focus:border-sidebarprimary focus:ring-sidebarprimary"
        :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
      ></textarea>
    </div>
  </div>
  <div class="mt-5">
    <p class="text-xs font-medium text-gray-500 mb-3">DIAGRAM</p>
    <div v-if="props.selectedDefinition?.processes && props.selectedDefinition.processes.length > 0">
      <FlowDiagram :processes="props.selectedDefinition.processes" />
    </div>
    <div
      v-else
      class="rounded-md p-4 border transition-colors duration-200 text-center"
      :class="isDarkMode ? 'bg-[#181a1b] border-[#2c2f31] text-gray-300' : 'bg-gray-50 border-gray-200 text-gray-500'"
    >
      <p>No processes in this flow</p>
    </div>
  </div>
  <div class="flex items-center justify-center gap-3">
    <button 
      v-if="!isEditing"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-2"
      type="button"
      @click="startEdit"
    >
      Edit
    </button>
    <button
      v-if="isEditing"
      @click="saveChanges"
      type="button"
      class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-2"
    >
      Save
    </button>
  </div>
</template>
