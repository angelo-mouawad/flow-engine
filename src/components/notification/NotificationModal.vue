<script setup lang="ts">
import { computed, ref } from "vue";
import { useNotificationStore } from "../../stores/NotificationStore";
import { useThemeStore } from "../../stores/themeStore";

const { notifications, removeNotification } = useNotificationStore();
const isOpen = ref(false);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const notificationTypeClasses = computed(() =>
  isDarkMode.value
    ? {
        success: "border-green-500/40 bg-green-500/10 text-green-100",
        error: "border-red-500/40 bg-red-500/10 text-red-100",
        info: "border-blue-500/40 bg-blue-500/10 text-blue-100",
      }
    : {
        success: "border-green-200 bg-green-50 text-green-900",
        error: "border-red-200 bg-red-50 text-red-900",
        info: "border-blue-100 bg-blue-50 text-blue-900",
      }
);

// Open/close modal
const openModal = () => {
  isOpen.value = true;
};
const closeModal = () => {
  isOpen.value = false;
};

defineExpose({ openModal });
</script>

<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="fixed inset-0 bg-black/50 flex items-start justify-center z-[10000] p-4 pt-24"
      @click.self="closeModal"
    >
      <div
        class="rounded-xl p-5 w-full max-w-lg max-h-[70vh] overflow-y-auto shadow-2xl border transition-colors duration-200"
        :class="
          isDarkMode
            ? 'bg-[#1c1e1f] text-gray-100 border-[#2c2f31]'
            : 'bg-white text-gray-900 border-gray-200'
        "
      >
        <header
          class="flex justify-between items-center mb-4 pb-3 border-b transition-colors duration-200"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-100'"
        >
          <h2 class="text-xl font-semibold" :class="isDarkMode ? 'text-white' : 'text-gray-800'">
            Notifications
          </h2>
          <button
            @click="closeModal"
            class="text-xl p-1 -mr-2 transition-colors"
            :class="isDarkMode ? 'text-gray-400 hover:text-white' : 'text-gray-500 hover:text-gray-700'"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path
                fill-rule="evenodd"
                d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                clip-rule="evenodd"
              />
            </svg>
          </button>
        </header>

        <ul v-if="notifications.length" class="space-y-3 mt-2">
          <li
            v-for="n in notifications"
            :key="n.id"
            class="p-3 text-base rounded-lg border flex justify-between items-start transition-colors duration-200"
            :class="[
              isDarkMode ? 'bg-[#181a1b] border-[#2c2f31] text-gray-100' : 'bg-white border-gray-200 text-gray-800',
              notificationTypeClasses[n.type],
            ]"
          >
            <p class="flex-1 pr-3">{{ n.message }}</p>
            <button
              @click="removeNotification(n.id)"
              class="text-lg p-1 -mt-1 -mr-1 transition-colors"
              :class="isDarkMode ? 'text-gray-400 hover:text-white' : 'text-gray-400 hover:text-gray-600'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                <path
                  fill-rule="evenodd"
                  d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </li>
        </ul>

        <div v-else class="text-center py-8">
          <p class="text-base" :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'">No new notifications</p>
          <p class="text-sm mt-1" :class="isDarkMode ? 'text-gray-500' : 'text-gray-400'">You're all caught up! 🎉</p>
        </div>
      </div>
    </div>
  </Teleport>
</template>
