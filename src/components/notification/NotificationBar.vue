<script setup lang="ts">
import { useToast } from "vue-toastification";
import { useNotificationStore } from "../../stores/NotificationStore";

const toast = useToast();
const { addNotification } = useNotificationStore();

const notifySuccess = () => {
  const msg = "Operation completed successfully!";
  toast.success(msg);
  addNotification(msg, "success");
};

const notifyError = () => {
  const msg = "Something went wrong!";
  toast.error(msg);
  addNotification(msg, "error");
};

const notifyInfo = () => {
  const msg = "This is an informational message.";
  toast.info(msg);
  addNotification(msg, "info");
};
</script>

<template>
  <div
    class="fixed top-1/2 right-4 -translate-y-1/2 flex flex-col gap-2 z-[9999]"
  >
    <button
      @click="notifySuccess"
      class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-colors"
    >
      ✅ Success
    </button>

    <button
      @click="notifyError"
      class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-colors"
    >
      ❌ Error
    </button>

    <button
      @click="notifyInfo"
      class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-md transition-colors"
    >
      ℹ️ Info
    </button>
  </div>
</template>
