<script setup lang="ts">
import NotificationBar from "../components/NotificationBar.vue";

const gridElements = [
  "Flow Definitions",
  "Flow Instances",
  "Diagram",
  "Something",
];
</script>

<template>
  <NotificationBar />

  <div class="w-full p-5 rounded-md">
    <div class="rounded-lg border border-gray-300 bg-white p-6 shadow-sm">
      <p class="font-bold text-center text-[2rem] pb-5">Welcome Manager</p>
      <div class="grid grid-cols-2 gap-4">
        <div
          v-for="(box, index) in gridElements"
          :key="index"
          class="flex flex-col text-center"
        >
          <p class="mb-2 font-semibold">{{ box }}</p>
          <RouterLink to="">
            <div
              class="bg-[#b9b9b9] h-40 rounded hover:opacity-80 transition-opacity"
            ></div>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
