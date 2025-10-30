<script setup lang="ts">
import FlowCard from "../components/dashboard/FlowCard.vue";
import StatsCard from "../components/dashboard/StatsCard.vue";
import FlowInstancesTable from "../components/FlowInstancesTable.vue";
import NotificationBar from "../components/NotificationBar.vue";
</script>

<template>
  <NotificationBar />

  <div class="p-5">
    <p class=" pb-5 font-bold text-4xl">My Flows</p>
    <div class="grid grid-cols-6 gap-20">
      <FlowCard :is-create-new="true"/>
      <FlowCard title="Basic Absence Flow"/>
      <FlowCard title="Clocking Flow"/>
      <FlowCard title="Invoices Flow"/>
    </div>
    <p class="pt-20 pb-5 font-bold text-4xl">Overview of Flow Instances</p>
    <div class="grid grid-cols-6 gap-10 pb-10">
      <StatsCard label="Total" value="7" color="#ffc533"/>
      <StatsCard label="Active" value="3" color="#10b981"/>
      <StatsCard label="Pending" value="2" color="#f59e0b"/>
      <StatsCard label="Failed" value="2" color="#ef4444"/>
      <StatsCard label="Paused" value="0" color="#3b82f6"/>
    </div>
    <FlowInstancesTable/>
  </div>
</template>
