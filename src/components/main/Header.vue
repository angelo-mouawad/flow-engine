<script setup lang="ts">
import { computed, ref } from "vue";

import NotificationModal from "../notification/NotificationModal.vue";
import UserSwitch from "../user/UserSwitch.vue";
import { useThemeStore } from "../../stores/themeStore";

const notifModal = ref<InstanceType<typeof NotificationModal>>();

const openModal = () => {
  notifModal.value?.openModal();
};

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const toggleTheme = () => {
  themeStore.toggleTheme();
};

const notificationIcon = computed(() =>
  isDarkMode.value ? "/images/notification-white.png" : "/images/notification-black.png"
);

const emit = defineEmits<{
  (e: "role-changed", payload: string): void;
}>();
</script>

<template>
  <NotificationModal ref="notifModal" />

  <header
    :class="[
      'w-full border-b px-4 sm:px-6 flex items-center justify-end h-21 gap-2 transition-colors duration-300',
      isDarkMode ? 'bg-[#181a1b] border-gray-800 text-gray-100' : 'bg-white border-sidebarsecondary/30 text-gray-700'
    ]"
  >
    <button
      @click="toggleTheme"
      type="button"
      class="inline-flex items-center gap-2 sm:gap-3 px-2 sm:px-3 py-2 rounded-full text-sm font-medium transition-colors duration-200 shadow-sm"
      :class="
        isDarkMode
          ? 'bg-[#242628] text-white hover:bg-[#1f2122]'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
      "
      aria-label="Toggle color theme"
    >
      <span class="hidden sm:block">{{ isDarkMode ? 'Dark Mode' : 'Dark Mode' }}</span>
      <span
        class="relative inline-flex h-5 w-10 items-center rounded-full transition-colors duration-200"
        :class="isDarkMode ? 'bg-yellow-500' : 'bg-gray-300'"
      >
        <span
          class="inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200"
          :class="isDarkMode ? 'translate-x-5' : 'translate-x-1'"
        ></span>
      </span>
    </button>
    <button @click="openModal"
      :class="[
        'inline-flex items-center justify-center w-9 h-9 sm:w-auto sm:h-10 sm:px-4 rounded-full sm:rounded-md focus:outline-none transition-colors shadow-sm ml-1 sm:ml-3',
        isDarkMode
          ? 'bg-[#1c1e1f] text-gray-100 hover:bg-[#1f2122]'
          : 'bg-white text-gray-700 hover:bg-gray-50'
      ]"
      aria-label="Notifications">
      <img :src="notificationIcon" alt="Notifications" class="w-5 h-5 sm:mr-2" />
      <span class="hidden sm:inline-flex items-center px-2">Notifications</span>
    </button>
    <UserSwitch @role-changed="$emit('role-changed', $event)" />
  </header>
</template>
