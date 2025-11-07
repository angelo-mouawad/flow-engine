<script setup lang="ts">
import { ref } from "vue";

import NotificationModal from "./notification/NotificationModal.vue";
import UserSwitch from "./user/UserSwitch.vue";

const notifModal = ref<InstanceType<typeof NotificationModal>>();

const openModal = () => {
  notifModal.value?.openModal();
};
</script>

<template>
  <NotificationModal ref="notifModal" />

  <header
    class="w-full bg-white border-b border-gray-300 px-6 py-4 flex items-center justify-end shadow-sm h-25 gap-2"
  >
    <button
      @click="openModal"
      class="inline-flex items-center gap-2 bg-white border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-100 px-4 py-2 rounded-md foxus:outline-none"
    >
      <!-- <span class="text-xl">🛎</span> -->
      <span class="font-medium">Notifications</span>
    </button>
    <UserSwitch @role-changed="$emit('role-changed', $event)"/>
  </header>
</template>
