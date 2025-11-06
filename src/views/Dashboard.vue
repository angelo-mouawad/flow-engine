<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import FlowCard from "../components/dashboard/FlowCard.vue";
import StatsCard from "../components/dashboard/StatsCard.vue";
import FlowInstancesTable from "../components/FlowInstancesTable.vue";
import NotificationBar from "../components/NotificationBar.vue";
import type { FlowInstance, Status } from "../types";
import FlowInstanceService from "../services/FlowInstanceService";

const flowInstances = ref<FlowInstance[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
let pollInterval: number; // TODO - implement polling

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
</script>

<template>
  <NotificationBar />

  <div class="p-4 w-full -mt-10">
    <div class="mb-8">
      <h2 class="pb-4 font-bold text-4xl">My Flows</h2>

      <div class="overflow-x-auto -mx-4 px-4">
        <div class="flex flex-row gap-4 pb-4">
          <div class="flex-shrink-0">
            <FlowCard :is-create-new="true" />
          </div>
          <FlowCard title="Basic Absence Flow" class="flex-shrink-0" />
          <FlowCard title="Clocking Flow" class="flex-shrink-0" />
          <FlowCard title="Invoices Flow" class="flex-shrink-0" />
        </div>
      </div>
    </div>

    <div class="mb-8">
      <h2 class="pb-4 font-bold text-4xl">Overview of Flow Instances</h2>
      <div class="grid grid-cols-6 gap-auto">
        <StatsCard
          label="Total"
          :value="stats.total.toString()"
          color="#ffc533"
        />
        <StatsCard
          label="Active"
          :value="stats.active.toString()"
          color="#10b981"
        />
        <StatsCard
          label="Pending"
          :value="stats.pending.toString()"
          color="#f59e0b"
        />
        <StatsCard
          label="Failed"
          :value="stats.failed.toString()"
          color="#ef4444"
        />
        <StatsCard
          label="Paused"
          :value="stats.paused.toString()"
          color="#3b82f6"
        />
        <StatsCard
          label="Success"
          :value="stats.success.toString()"
          color="#84cc16"
        />
      </div>
    </div>

    <FlowInstancesTable class="bg-white rounded-lg shadow-lg" />
  </div>
</template>
