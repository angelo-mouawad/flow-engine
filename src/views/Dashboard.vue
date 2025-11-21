<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import FlowCard from "../components/dashboard/FlowCard.vue";
import StatsCard from "../components/dashboard/StatsCard.vue";
import FlowInstancesTable from "../components/flows/FlowInstancesTable.vue";
// import NotificationBar from "../components/notification/NotificationBar.vue";
import type { FlowInstance, Status } from "../types";
import FlowInstanceService from "../services/FlowInstanceService";
import { useThemeStore } from "../stores/themeStore";

const flowInstances = ref<FlowInstance[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
//let pollInterval: number; // TODO - implement polling

const stats = computed(() => {
  const statuses: Record<Status, number> = {
    ACTIVE: 0,
    PENDING: 0,
    FAILURE: 0,
    PAUSED: 0,
    SUCCESS: 0,
  };

  flowInstances.value.forEach((instance) => {
    statuses[instance.status]++;
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

onMounted(() => {
  fetchFlowInstances();
});

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div
    class="p-2 sm:p-4 w-full transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div class="mb-8">
      <h2 class="pb-2 font-bold text-2xl sm:pb-4 sm:text-4xl">My Flows</h2>

      <div class="overflow-x-auto">
        <div class="flex flex-row gap-5 pb-2 min-w-[600px]">
          <FlowCard :is-create-new="true" class="flex-shrink-0 w-60" />
          <FlowCard title="Basic Absence Flow" class="flex-shrink-0 w-60" />
          <FlowCard title="Clocking Flow" class="flex-shrink-0 w-60" />
          <FlowCard title="Invoices Flow" class="flex-shrink-0 w-60" />
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
