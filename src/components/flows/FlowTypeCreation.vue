<script setup lang="ts">
import { computed, ref } from "vue";
import FlowDefinitionsTable from "./FlowDefinitionsTable.vue";
import FlowInstancesTable from "./FlowInstancesTable.vue";
import FilterButton from "./FilterButton.vue";
import { useThemeStore } from "../../stores/themeStore";

const props = defineProps(["isDefinition"]);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const searchQuery = ref("");
const activeFilters = ref<Record<string, boolean>>({});
const filtersVisible = ref(false);
const filterPanelTargetId = "flow-definitions-filter-panel";
const filterPanelTargetSelector = `#${filterPanelTargetId}`;

const handleFilterChange = (payload: Record<string, boolean>) => {
  activeFilters.value = { ...payload };
};

const handleFilterVisibility = (visible: boolean) => {
  filtersVisible.value = visible;
};
</script>

<template>
  <div
    class="w-full rounded-md p-2 sm:p-5 transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div
      class="rounded-lg border p-2 sm:p-6 shadow-sm transition-colors duration-300"
      :class="
        isDarkMode
          ? 'bg-[#1c1e1f] border-[#2c2f31]'
          : 'bg-white border-gray-300'
      "
    >
      <h2 class="font-bold text-2xl sm:text-4xl py-3 sm:py-5">
        {{ props.isDefinition ? "Flow Definitions: " : "Flow Instances: " }}
      </h2>

      <div
        class="flex flex-col sm:flex-row items-stretch sm:items-center gap-2 sm:gap-4 mb-3 sm:justify-between"
      >
        <div class="relative flex-1 w-full max-w-md">
          <svg
            class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search"
            class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none transition-colors w-full"
            :class="
              isDarkMode
                ? 'bg-[#1c1e1f] border-[#2c2f31] text-white placeholder-gray-500'
                : 'bg-white border-gray-300 text-gray-800 placeholder-gray-400'
            "
          />
        </div>

        <div
          v-if="props.isDefinition"
          class="flex items-center gap-2 w-full sm:w-auto sm:ml-auto sm:justify-end"
        >
          <FilterButton
            :panel-target="filterPanelTargetSelector"
            @change="handleFilterChange"
            @visibility-change="handleFilterVisibility"
          />
          <RouterLink to="/flow-definitions/new">
            <button
              class="rounded-md px-4 py-1.5 bg-yellow-500 text-sm font-medium cursor-pointer hover:bg-yellow-600 flex items-center gap-2"
            >
              <svg
                class="w-4 h-4 text-gray-800"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 4v16m8-8H4"
                />
              </svg>
              <span class="text-gray-800">New flow</span>
            </button>
          </RouterLink>
        </div>
      </div>

      <div
        v-if="props.isDefinition"
        class="mt-2 flex flex-col gap-4 lg:flex-row items-start"
      >
        <div
          class="flex-1 overflow-x-auto rounded-md shadow-sm border transition-colors duration-300"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
        >
          <FlowDefinitionsTable
            :searchQuery="searchQuery"
            :filters="activeFilters"
          />
        </div>
        <div
          :id="filterPanelTargetId"
          class="overflow-hidden transition-all duration-300"
          :style="{
            width: filtersVisible ? '220px' : '0px',
            opacity: filtersVisible ? 1 : 0,
          }"
        ></div>
      </div>

      <div
        v-else
        class="overflow-x-auto rounded-md shadow-sm border mt-2 transition-colors duration-300"
        :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
      >
        <FlowInstancesTable :searchQuery="searchQuery" />
      </div>
    </div>
  </div>
</template>
