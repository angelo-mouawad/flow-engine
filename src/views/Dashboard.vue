<script setup lang="ts">
import { computed, onUnmounted, ref, watch } from "vue";
import FlowCard from "../components/dashboard/FlowCard.vue";
import StatsCard from "../components/dashboard/StatsCard.vue";
import FlowInstancesTable from "../components/flows/FlowInstancesTable.vue";
// import NotificationBar from "../components/notification/NotificationBar.vue";
import type { FlowInstance, Status, FlowDefinition } from "../types";
import FlowInstanceService from "../services/FlowInstanceService";
import FlowDefinitionService from "../services/FlowDefinitionService";
import { useThemeStore } from "../stores/themeStore";
import { useUserStore } from "../stores/userStore";

const userStore = useUserStore();
const isManager = () => userStore.isManager;

const flowInstances = ref<FlowInstance[]>([]);
const flowDefinitions = ref<FlowDefinition[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
let pollInterval: number;

const stats = computed(() => {
  const statuses: Record<Status, number> = {
    ACTIVE: 0,
    PENDING: 0,
    FAILURE: 0,
    PAUSED: 0,
    SUCCESS: 0,
  };

  flowInstances.value.forEach((instance) => {
    statuses[instance.flowStatus]++;
  });

  return {
    total: flowInstances.value.length,
    active: statuses.ACTIVE,
    pending: statuses.PENDING,
    failed: statuses.FAILURE,
    paused: statuses.PAUSED,
    success: statuses.SUCCESS,
  };
});

const fetchFlowInstances = async () => {
  if (!userStore.currentUser) return;

  loading.value = true;
  error.value = null;
  try {
    const data = await FlowInstanceService.getFlowInstances();
    flowInstances.value = data;
  } catch (e) {
    error.value =
      e instanceof Error
        ? e.message
        : "An error occurred while fetching flow instances";
    console.error("Failed to fetch flow instances:", e);
  } finally {
    loading.value = false;
  }
};

const fetchFlowDefinitions = async () => {
  if (!userStore.currentUser) return;

  try {
    const defs = await FlowDefinitionService.getFlowDefinitions();
    flowDefinitions.value = defs;
  } catch (e) {
    console.error("Failed to fetch flow definitions:", e);
  }
};

watch(
  () => userStore.currentUser,
  (newUser) => {
    if (newUser) {
      fetchFlowInstances();
      fetchFlowDefinitions();
      if (!pollInterval) {
        pollInterval = window.setInterval(fetchFlowInstances, 5000);
      }
    } else {
      flowDefinitions.value = [];
      flowInstances.value = [];
      clearInterval(pollInterval);
      pollInterval = 0;
    }
  },
  { immediate: true }
)

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
onUnmounted(() => {
  clearInterval(pollInterval);
})
</script>

<template>
  <div
    class="p-2 sm:p-4 w-full transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div v-if="isManager()" class="mb-8">
      <h2 class="pb-2 font-bold text-2xl sm:pb-4 sm:text-4xl">My Flows</h2>

      <div class="overflow-x-auto">
        <div class="flex flex-row gap-5 pb-2 min-w-[600px]">
          <FlowCard is-create-new class="flex-shrink-0 w-60" />
          <FlowCard
            v-for="def in flowDefinitions"
            :key="def.id ?? def.title"
            :id="def.id"
            :title="def.title"
            :description="def.description"
            :processes="def.processes"
            class="flex-shrink-0 w-60"
          />
        </div>
      </div>
    </div>

    <div class="mb-8">
      <h2 class="pb-2 font-bold text-2xl sm:pb-4 sm:text-4xl">Overview of Flow Instances</h2>
      <div class="overflow-x-auto">
        <div class="flex flex-row gap-5 pb-2 min-w-[540px]">
          <StatsCard label="Total" :value="stats.total.toString()" color="#ffc533" class="flex-shrink-0 w-44" />
          <StatsCard label="Active" :value="stats.active.toString()" color="#10b981" class="flex-shrink-0 w-44" />
          <StatsCard label="Pending" :value="stats.pending.toString()" color="#f59e0b" class="flex-shrink-0 w-44" />
          <StatsCard label="Failed" :value="stats.failed.toString()" color="#ef4444" class="flex-shrink-0 w-44" />
          <StatsCard label="Paused" :value="stats.paused.toString()" color="#3b82f6" class="flex-shrink-0 w-44" />
          <StatsCard label="Success" :value="stats.success.toString()" color="#84cc16" class="flex-shrink-0 w-44" />
        </div>
      </div>
    </div>

    <div
      class="overflow-x-auto rounded-md transition-colors duration-300 border"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-200'"
    >
      <FlowInstancesTable />
    </div>
  </div>
</template>
