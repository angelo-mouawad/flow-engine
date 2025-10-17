<script setup lang="ts">
import NotificationBar from '../components/NotificationBar.vue'
import NotificationModal from '../components/NotificationModal.vue'
import { ref } from 'vue'

const gridElements = [
  "Flow Definitions",
  "Flow Instances",
  "Diagram",
  "Something",
]

const notifModal = ref<InstanceType<typeof NotificationModal>>()

const openModal = () => {
  notifModal.value?.openModal()
}
</script>

<template>
  <NotificationBar />

  <NotificationModal ref="notifModal" />

  <button
    @click="openModal"
    class="fixed top-6 right-6 bg-gray-800 text-white px-4 py-2 rounded-lg shadow-md hover:bg-gray-700"
  >
    🛎 Notifications
  </button>

  <div class="w-full p-5 rounded-md">
    <div class="rounded-lg border bg-white p-6">
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
