<script setup lang="ts">
import { ref } from "vue";

import NotificationModal from "../components/NotificationModal.vue";

const notifModal = ref<InstanceType<typeof NotificationModal>>();

const openModal = () => {
  notifModal.value?.openModal();
};
</script>

<template>
  <NotificationModal ref="notifModal" />

  <header
    class="w-full bg-white border-b border-gray-300 px-6 py-4 flex items-center justify-end shadow-sm"
  >
    <button
      @click="openModal"
      class="flex items-center gap-2 bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg transition-colors"
    >
      <span class="text-xl">🛎</span>
      <span class="font-medium">Notifications</span>
    </button>
  </header>
</template>
