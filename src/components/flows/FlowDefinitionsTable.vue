<script setup lang="ts">
import type { FlowDefinition } from "../../types";
import { ref, onMounted, computed } from "vue";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import FlowDetails from "./FlowDetails.vue";
import deleteIcon from "/images/delete1.png.webp";
import { useThemeStore } from "../../stores/themeStore";

const loading = ref(false);
const flowDefinitions = ref<FlowDefinition[]>([]);
const error = ref<string | null>(null);

const selectedDefinition = ref<FlowDefinition | null>(null);
const showModal = ref(false);

const sortKey = ref<"title" | "updatedAt">("updatedAt");
const sortOrder = ref<"asc" | "desc">("asc");

const sortFlowDefinitions = (key: "title" | "updatedAt") => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "asc";
  }
  flowDefinitions.value = [...flowDefinitions.value].sort((a, b) => {
    let result = 0;
    if (key === "title") {
      if (a.title.toLowerCase() < b.title.toLowerCase()) result = -1;
      if (a.title.toLowerCase() > b.title.toLowerCase()) result = 1;
    } else if (key === "updatedAt" && a.updatedAt && b.updatedAt) {
      result =
        new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime();
    }
    return sortOrder.value === "asc" ? result : -result;
  });
};

const fetchFlowDefinitions = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await FlowDefinitionService.getFlowDefinitions();
    flowDefinitions.value = data;
    sortFlowDefinitions(sortKey.value);
  } catch (e) {
    error.value =
      e instanceof Error
        ? e.message
        : "An error occurred while fetching flow definitions";
    console.error("Failed to fetch flow definitions:", e);
  } finally {
    loading.value = false;
  }
};

const onRowIconClick = (def: FlowDefinition) => {
  // Remove the clicked definition from the local array so the row disappears
  flowDefinitions.value = flowDefinitions.value.filter((d) => d.id !== def.id);
};

onMounted(() => {
  fetchFlowDefinitions();
});

const onRowClick = (def: FlowDefinition) => {
  selectedDefinition.value = def;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedDefinition.value = null;
};

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div class="w-full transition-colors duration-300" :class="isDarkMode ? 'text-white' : 'text-gray-900'">
    <table class="w-full border-collapse text-sm transition-colors duration-200" :class="isDarkMode ? 'text-white' : 'text-gray-900'">
      <thead>
        <tr class="text-left text-white bg-[#111]">
          <th
            class="px-4 py-2 font-medium select-none cursor-pointer"
            @click="sortFlowDefinitions('title')"
          >
            Name
            <span class="ml-2">
              <span
                class="ml-2 inline-block"
                style="width: 1.5em; text-align: left"
              >
                <span v-if="sortKey === 'title'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class="px-4 py-2 font-medium" style="width: 40%">Description</th>
          <th
            class="px-4 py-2 font-medium select-none cursor-pointer"
            @click="sortFlowDefinitions('updatedAt')"
          >
            Last Updated
            <span class="ml-2">
              <span
                class="ml-2 inline-block"
                style="width: 1.5em; text-align: left"
              >
                <span v-if="sortKey === 'updatedAt'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class="px-4 py-2"></th>
        </tr>
      </thead>
      <tbody class="divide-y" :class="isDarkMode ? 'divide-[#2c2f31]' : 'divide-gray-200'">
        <tr v-if="loading">
          <td colspan="4" class="text-center py-8">
            <div :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading flow definitions...</div>
          </td>
        </tr>
        <tr v-else-if="error">
          <td colspan="4" class="p-4">
            <div
              class="px-4 py-3 rounded border"
              :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'"
            >
              <p class="font-medium">Error</p>
              <p class="text-sm">{{ error }}</p>
            </div>
          </td>
        </tr>
        <tr v-else-if="flowDefinitions.length === 0">
          <td colspan="4" class="text-center py-8" :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'">
            No flow definitions found.
          </td>
        </tr>
        <tr
          v-else
          v-for="def in flowDefinitions"
          :key="def.id"
          class="cursor-pointer transition-colors"
          :class="isDarkMode ? 'hover:bg-[#242628]' : 'hover:bg-gray-50'"
          @click="onRowClick(def)"
        >
          <td class="px-4 py-2 font-medium">{{ def.title }}</td>
          <td class="px-4 py-2 truncate" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'" style="max-width: 320px">
            {{ def.description || "—" }}
          </td>
          <td class="px-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
            {{
              new Date(def.updatedAt??'Never').toLocaleDateString("en-GB", {
                year: "numeric",
                month: "long",
                day: "numeric",
              })
            }}
          </td>
          <td class="px-4 py-2 text-right min-w-[72px]">
            <button
              @click.stop="onRowIconClick(def)"
              class="inline-flex items-center p-1 rounded focus:ring-2 focus:ring-blue-400 transition-colors"
              :class="isDarkMode ? 'hover:bg-[#1c1e1f]' : 'hover:bg-gray-100'"
              :aria-label="`Delete ${def.title}`"
            >
              <img :src="deleteIcon" alt="Delete" class="w-5 h-5" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div
      v-if="showModal"
      class="fixed left-1/2 top-1/2 w-[95vw] sm:w-[80vw] md:w-[60%] md:h-[80%] max-w-2xl -translate-x-1/2 -translate-y-1/2 rounded-md shadow-lg ring-1 ring-gray-800/10 z-50 transition-colors duration-200"
      :class="isDarkMode ? 'bg-[#1c1e1f] border border-[#4b4f53] text-white' : 'bg-white border border-gray-300 text-gray-900'"
    >
      <div class="p-6 h-full overflow-y-auto relative">
        <button
          @click="closeModal"
          class="absolute cursor-pointer right-4 top-4 rounded-md"
          :class="isDarkMode ? 'text-gray-300 hover:text-white' : 'text-gray-600 hover:text-gray-900'"
        >
          ✕
        </button>
        <FlowDetails :selected-definition="selectedDefinition"/>
      </div>
    </div>

    <div
      v-if="showModal"
      @click="closeModal"
      class="fixed inset-0 bg-black opacity-50 z-40"
    ></div>
  </div>
</template>
