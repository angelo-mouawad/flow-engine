<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import { useRoleStore } from "../../stores/roleStore";
import { useThemeStore } from "../../stores/themeStore";
import { useUserStore } from "../../stores/userStore";

const roleStore = useRoleStore();
const userStore = useUserStore();
const themeStore = useThemeStore();

const isOpen = ref(false);

const dropDownRef = ref<HTMLElement | null>(null);

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const handleClickOutside = (event: MouseEvent) => {
  if (dropDownRef.value && !dropDownRef.value.contains(event.target as Node)) {
    isOpen.value = false;
  }
};

onMounted(() => document.addEventListener("click", handleClickOutside));
onBeforeUnmount(() => document.removeEventListener("click", handleClickOutside));

const userIcon = computed(() =>
  themeStore.isDarkMode ? "/images/user-white.png" : "/images/user-black.png"
);

const currentUserName = computed(() => userStore.currentUser?.name ?? "Select User");
const currentUserRole = computed(() => userStore.currentUser?.role ?? "User");

const setUser = (name: string) => {
  userStore.login(name);

  if (userStore.currentUser) {
    roleStore.setRole(userStore.currentUser.role);
  }

  isOpen.value = false;
  window.location.reload();
}

</script>

<template>
  <div class="flex flex-col items-end ml-2 sm:ml-4">
    <div class="relative inline-block text-left" ref="dropDownRef">
      <button
        @click="toggleDropdown"
        type="button"
        class="inline-flex items-center justify-center w-9 h-9 sm:w-auto sm:h-10 sm:px-4 rounded-full sm:rounded-md focus:outline-none transition-colors shadow-sm"
        :class="themeStore.isDarkMode ? 'bg-[#1c1e1f] text-gray-100 hover:bg-[#1f2122]' : 'bg-white text-gray-700 hover:bg-gray-50'"
      >
        <img :src="userIcon" alt="User" class="w-5 h-5 sm:mr-2" />
        <span class="hidden sm:inline-flex items-center px-2 gap-1 sm:min-w-[110px] justify-between">
          <span class="font-semibold">{{ currentUserName }}</span>
          <span class="text-xs opacity-70">{{ currentUserRole }}</span>
        </span>
        <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path
              fill-rule="evenodd"
              d="M5.23 7.21a.75.75 0 011.06.02L10 10.584l3.71-3.354a.75.75 0 111.02 1.096l-4.25 3.84a.75.75 0 01-1.02 0l-4.25-3.84a.75.75 0 01.02-1.096z"
              clip-rule="evenodd"
            />
          </svg>
      </button>
      <div
        v-if="isOpen"
        class="absolute right-0 mt-1 w-32 origin-top-right rounded-md py-1 text-sm shadow-lg ring-1 ring-black/10"
        :class="themeStore.isDarkMode ? 'bg-[#1c1e1f] text-gray-100' : 'bg-white text-gray-700'"
      >
        <button
          v-for="user in userStore.users"
          :key="user.name"
          @click="setUser(user.name)"
          class="block w-full px-3 py-1.5 text-left transition-colors"
          :class="[
            userStore.currentUser?.name === user.name ? 'text-sidebarprimary font-semibold' : '',
            themeStore.isDarkMode? 'hover:bg-[242628]' : 'hover:bg-gray-100'
          ]"
        >
          {{ user.name }} ({{ user.role }})
        </button>
      </div>
    </div>
  </div>
</template>