<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import { useRoleStore } from '../../stores/roleStore';

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
</script>

<template>
  <div class="flex flex-col items-end">
    <div class="relative inline-block text-left" ref="dropDownRef">
      <button
        @click="toggleDropdown"
        type="button"
        class="inline-flex justify-center items-center w-9 h-9 sm:w-32 sm:h-10 bg-white text-gray-700 hover:bg-gray-50 rounded-full sm:rounded-md focus:outline-none transition-colors"
      >
        <img src="/images/user.png" alt="User" class="w-5 h-5 sm:mr-2" />
        <span class="hidden sm:inline-flex items-center pl-2 pr-0">
          <span class="mr-2">{{ roleStore.role }}</span>
          <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path
              fill-rule="evenodd"
              d="M5.23 7.21a.75.75 0 011.06.02L10 10.584l3.71-3.354a.75.75 0 111.02 1.096l-4.25 3.84a.75.75 0 01-1.02 0l-4.25-3.84a.75.75 0 01.02-1.096z"
              clip-rule="evenodd"
            />
          </svg>
        </span>
      </button>
      <div
        v-if="isOpen"
        class="absolute right-0 z-10 mt-1 w-32 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 py-1"
      >
        <button
          @click="setRole('User')"
          class="block w-full px-3 py-1.5 text-sm hover:bg-gray-100 text-left"
          :class="{'text-sidebarprimary': roleStore.role === 'User'}"
        >
          User
        </button>
        <button
          @click="setRole('Manager')"
          class="block w-full px-3 py-1.5 text-sm hover:bg-gray-100 text-left"
          :class="{'text-sidebarprimary': roleStore.role === 'Manager'}"
        >
          Manager
        </button>
      </div>
    </div>
  </div>
</template>
