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
    class="w-full bg-white border-b border-sidebarsecondary/30 px-4 sm:px-6 flex items-center justify-end h-14 gap-0">
    <button @click="openModal"
      class="inline-flex items-center justify-center w-9 h-9 sm:w-auto sm:h-10 sm:px-4 bg-white text-gray-700 hover:bg-gray-50 rounded-full sm:rounded-md focus:outline-none transition-colors"
      aria-label="Notifications">
      <img src="/images/notification.png" alt="Notifications" class="w-5 h-5 sm:mr-2" />
      <span class="hidden sm:inline-flex items-center px-2">Notifications</span>
    </button>
    <UserSwitch @role-changed="$emit('role-changed', $event)" />
  </header>
</template>
