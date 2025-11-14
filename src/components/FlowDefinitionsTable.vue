<script setup lang="ts">
import type { FlowDefinition } from "../types";
import { ref, onMounted } from "vue";
import FlowDefinitionService from "../services/FlowDefinitionService";
import deleteIcon from "/images/delete1.png.webp";

const loading = ref(false);
const flowDefinitions = ref<FlowDefinition[]>([]);
const error = ref<string | null>(null);

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
    } else if (key === "updatedAt") {
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
const selectedDefinition = ref<FlowDefinition | null>(null);
const showModal = ref(false);

const onRowClick = (def: FlowDefinition) => {
  selectedDefinition.value = def;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedDefinition.value = null;
};

</script>

<template>
  <div class="w-full">
    <table class="w-full border-collapse text-sm">
      <thead>
        <tr class="text-left text-white bg-[#110]">
          <th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowDefinitions('title')">
            Name
            <span class="ml-2">
              <span class="ml-2 inline-block" style="width: 1.5em; text-align: left">
                <span v-if="sortKey === 'title'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class="px-4 py-2 font-medium" style="width: 40%">Description</th>
          <th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowDefinitions('updatedAt')">
            Last Updated
            <span class="ml-2">
              <span class="ml-2 inline-block" style="width: 1.5em; text-align: left">
                <span v-if="sortKey === 'updatedAt'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class=""></th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-200">
        <tr v-if="loading">
          <td colspan="4" class="text-center py-8">
            <div class="text-gray-600">Loading flow definitions...</div>
          </td>
        </tr>
        <tr v-else-if="error">
          <td colspan="4" class="p-4">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              <p class="font-medium">Error</p>
              <p class="text-sm">{{ error }}</p>
            </div>
          </td>
        </tr>
        <tr v-else-if="flowDefinitions.length === 0">
          <td colspan="4" class="text-center py-8 text-gray-500">
            No flow definitions found.
          </td>
        </tr>
        <tr v-else v-for="def in flowDefinitions" :key="def.id"
          class="cursor-pointer hover:bg-gray-50 transition-colors" @click="onRowClick(def)">
          <td class="px-4 py-2 font-medium">{{ def.title }}</td>
          <td class="px-4 py-2 text-gray-600 truncate" style="max-width: 320px">{{ def.description || "—" }}</td>
          <td class="px-4 py-2 text-gray-600">
            {{
              new Date(def.updatedAt).toLocaleDateString("en-GB", {
                year: "numeric",
                month: "long",
                day: "numeric",
              })
            }}
          </td>
          <td class="px-4 py-2 text-right">
            <button @click.stop="onRowIconClick(def)"
              class="inline-flex items-center p-1 rounded hover:bg-gray-100 focus:ring-2 focus:ring-blue-400"
              :aria-label="`Delete ${def.title}`">
              <img :src="deleteIcon" alt="Delete" class="w-5 h-5" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal"
      class="absolute left-1/2 top-1/2 w-[60%] -translate-x-1/2 -translate-y-1/2 rounded-md border border-gray-300 bg-white shadow-lg ring-1 ring-gray-200 z-50">
      <div class="p-6 h-full overflow-y-auto relative">
        <button @click="closeModal"
          class="absolute cursor-pointer right-4 top-4 rounded-md text-gray-600 hover:text-gray-900">✕</button>
        <h2 class="text-2xl font-semibold mb-4 text-center">Manage this request</h2>

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div v-for="(label, key) in {
            'Name': selectedDefinition?.title,
            'Updated At': selectedDefinition?.updatedAt,
            'Description': selectedDefinition?.description
          }" :key="key" class="bg-gray-50 border border-gray-200 rounded-md p-4">
            <div class="text-xs font-medium text-gray-500">{{ key }}</div>
            <div class="mt-1 text-lg font-semibold text-gray-800">{{ label || 'no value' }}</div>
          </div>
        </div>
        <div class="mt-5">
          <div v-for="(label, key) in {
            'Diagram': selectedDefinition?.title
          }" :key="key" class="bg-gray-50 border border-gray-200 rounded-md p-4">
            <div class="text-xs font-medium text-gray-500">{{ key }}</div>
            <div class="mt-1 text-lg font-semibold text-gray-800">{{ label || 'no value' }}</div>
          </div>
        </div>
        <div class="text-center">
          <button type="button"
            class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer mt-5">
            Edit
          </button>
        </div>
      </div>
    </div>

    <div v-if="showModal" @click="closeModal" class="h-screen w-screen absolute left-0 top-0 bg-black opacity-50"></div>
  </div>

</template>