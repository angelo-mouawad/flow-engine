<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
  import { useRoleStore } from '../../stores/roleStore';
  import { useThemeStore } from '../../stores/themeStore';

  const isOpen = ref(false);

  const emit = defineEmits<{
    (e: 'role-changed', role: 'User' | 'Manager'): void;
  }>()

  const roleStore = useRoleStore();

  const setRole = (role: 'User' | 'Manager') => {
    roleStore.setRole(role);
    isOpen.value = false;
    emit('role-changed', role)
  }

  const toggleDropdown = () => {
    isOpen.value = !isOpen.value;
  }

  const dropDownRef = ref<HTMLElement | null>(null);

  const handleClickOutside = (event: MouseEvent) => {
    if (dropDownRef.value && !dropDownRef.value.contains(event.target as Node)) {
        isOpen.value = false;
    }
  }

  onMounted(() => document.addEventListener('click', handleClickOutside));
  onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside));

  const themeStore = useThemeStore();
  const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div class="relative inline-block text-left" ref="dropDownRef">
    <button
      @click="toggleDropdown"
      type="button"
      class="inline-flex items-center justify-center w-9 h-9 sm:w-auto sm:h-10 sm:px-4 rounded-full sm:rounded-md focus:outline-none transition-colors shadow-sm"
      :class="isDarkMode ? 'bg-[#1c1e1f] text-gray-100 hover:bg-[#1f2122]' : 'bg-white text-gray-700 hover:bg-gray-50'"
    >
      <img src="/images/user.png" alt="User" class="w-5 h-5 sm:mr-2" />
      <span class="hidden sm:inline-flex items-center px-2 gap-1">
        <span>{{ roleStore.role }}</span>
        <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M5.23 7.21a.75.75 0 011.06.02L10 10.584l3.71-3.354a.75.75 0 111.02 1.096l-4.25 3.84a.75.75 0 01-1.02 0l-4.25-3.84a.75.75 0 01.02-1.096z"
            clip-rule="evenodd"
          />
        </svg>
      </span>
    </button>
    <transition name="fade">
      <div
        v-if="isOpen"
        class="absolute right-0 mt-2 w-44 origin-top-right rounded-2xl py-2 transition-all z-20 shadow-[0_20px_40px_rgba(0,0,0,0.15)] ring-1 ring-black/5 backdrop-blur-md overflow-hidden"
        :class="isDarkMode ? 'bg-[#1f2224]/95' : 'bg-white/95'"
      >
        <p class="px-4 pb-2 text-[0.65rem] uppercase tracking-[0.3em] font-semibold"
          :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">Choose role</p>
        <div class="space-y-1 px-2">
          <button
            @click="setRole('User')"
            class="flex items-center w-full gap-3 px-3 py-2 text-sm rounded-xl transition-all"
            :class="[
              roleStore.role === 'User' ? 'bg-sidebarprimary/10 text-sidebarprimary shadow-inner' : '',
              isDarkMode ? 'text-gray-200 hover:bg-[#2a2d2f]' : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            <span class="w-6 h-6 rounded-full flex items-center justify-center"
              :class="roleStore.role === 'User' ? 'bg-sidebarprimary/20 text-sidebarprimary' : isDarkMode ? 'bg-[#2a2d2f] text-gray-200' : 'bg-gray-100 text-gray-500'">
              👤
            </span>
            <span class="flex-1 text-left font-medium">User</span>
            <span
              v-if="roleStore.role === 'User'"
              class="text-sidebarprimary text-xs font-bold"
            >
              ✓
            </span>
          </button>
          <button
            @click="setRole('Manager')"
            class="flex items-center w-full gap-3 px-3 py-2 text-sm rounded-xl transition-all"
            :class="[
              roleStore.role === 'Manager' ? 'bg-sidebarprimary/10 text-sidebarprimary shadow-inner' : '',
              isDarkMode ? 'text-gray-200 hover:bg-[#2a2d2f]' : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            <span class="w-6 h-6 rounded-full flex items-center justify-center"
              :class="roleStore.role === 'Manager' ? 'bg-sidebarprimary/20 text-sidebarprimary' : isDarkMode ? 'bg-[#2a2d2f] text-gray-200' : 'bg-gray-100 text-gray-500'">
              🧭
            </span>
            <span class="flex-1 text-left font-medium">Manager</span>
            <span
              v-if="roleStore.role === 'Manager'"
              class="text-sidebarprimary text-xs font-bold"
            >
              ✓
            </span>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>
