<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import { useRoleStore } from '../../stores/roleStore';

  const isOpen = ref(false);

  const emit = defineEmits<{
    (e: 'role-changed', role: 'User' | 'Admin'): void;
  }>()

  const roleStore = useRoleStore();

  const setRole = (role: 'User' | 'Admin') => {
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
        class="inline-flex w-full justify-between items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 focus:outline-none"
        >
        {{ roleStore.role }}
        <svg class="ml-2 h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path
            fill-rule="evenodd"
            d="M5.23 7.21a.75.75 0 011.06.02L10 10.584l3.71-3.354a.75.75 0 111.02 1.096l-4.25 3.84a.75.75 0 01-1.02 0l-4.25-3.84a.75.75 0 01.02-1.096z"
            clip-rule="evenodd"
            />
        </svg>
        </button>
        <div
        v-if="isOpen"
        class="absolute right-0 z-10 mt-2 w-36 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5"
        >
        <div class="py-1">
            <button
            @click="setRole('User')"
            class="block w-full px-4 py-2 text-left text-sm hover:bg-gray-100"
            >
            User
            </button>
            <button
            @click="setRole('Admin')"
            class="block w-full px-4 py-2 text-left text-sm hover:bg-gray-100"
            >
            Admin
            </button>
        </div>
        </div>
    </div>
  </div>
</template>
