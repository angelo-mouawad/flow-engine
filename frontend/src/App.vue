<script setup lang="ts">
import SideBar from "./components/main/SideBar.vue";
import Header from "./components/main/Header.vue";
import { computed, watch, watchEffect } from "vue";
import { useRoleStore } from "./stores/roleStore";
import { useThemeStore } from "./stores/themeStore";
import { useUserStore } from "./stores/userStore";
import { useNotificationStore } from "./stores/notificationStore";

const notificationStore = useNotificationStore();
const roleStore = useRoleStore();
const userStore = useUserStore();
const currentRole = computed({
  get: () => roleStore.role,
  set: (v) => roleStore.setRole(v),
});

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

if (typeof window !== "undefined") {
  watchEffect(() => {
    document.documentElement.classList.toggle("theme-dark", isDarkMode.value);
    document.body.classList.toggle("theme-dark", isDarkMode.value);
  });
};

watch(
  () => userStore.currentUser,
  (newUser) => {
    if (newUser) {
      notificationStore.connectWebSocket();
    } else {
      notificationStore.disconnectWebSocket();
    }
  },
  { immediate: true }
);

window.addEventListener("storage", (event) => {
  if (event.key === "user") {
    userStore.loadFromStorage();
  }
});
</script>

<template>
  <main
    :class="[
      'min-h-screen flex flex-col md:flex-row transition-colors duration-300',
      isDarkMode ? 'bg-[#1c1e1f] text-gray-100' : 'bg-mainbg text-gray-900'
    ]"
  >
    <SideBar />
    <div class="flex-1 flex flex-col min-h-screen w-full overflow-x-hidden">
      <div
        class="sticky top-0 z-20 transition-colors duration-300"
        :class="isDarkMode ? 'bg-[#181a1b]' : 'bg-white'"
      >
        <Header @role-changed="currentRole = $event" />
      </div>

      <div class="flex-1">
        <div class="w-full max-w-7xl mx-auto p-4 sm:p-6">
          <router-view />
        </div>
      </div>
    </div>
  </main>
</template>
