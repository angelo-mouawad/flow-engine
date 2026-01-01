<script setup lang="ts">
import { useNotificationStore } from "../../stores/notificationStore";

const notificationStore = useNotificationStore();

const notifySuccess = () => {
  notificationStore.addNotification("Operation completed successfully!", "success");
};

const notifyError = () => {
  notificationStore.addNotification("Something went wrong!", "error");
};

const notifyInfo = () => {
  notificationStore.addNotification("This is an informational message.", "info");
};
</script>

<template>
  <div
    class="fixed top-1/2 right-4 -translate-y-1/2 flex flex-col gap-2 z-[9999]"
  >
    <button
      @click="notifySuccess"
      class="bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-all active:scale-95"
    >
      ✅ Success
    </button>

    <button
      @click="notifyError"
      class="bg-red-500 hover:bg-red-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-all active:scale-95"
    >
      ❌ Error
    </button>

    <button
      @click="notifyInfo"
      class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-all active:scale-95"
    >
      ℹ️ Info
    </button>
  </div>
</template>

<style scoped>
button:active {
  transform: scale(0.95);
}
</style>