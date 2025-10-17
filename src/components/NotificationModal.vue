<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="p-6 fixed inset-0 bg-black/50 flex items-top justify-center z-[10000]"
      @click.self="closeModal"
    >
      <div class="bg-white rounded-xl p-6 w-240 max-h-[80vh] overflow-y-auto shadow-lg">
        <header class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-semibold">Notifications</h2>
          <button @click="closeModal" class="text-gray-500 hover:text-black text-lg">✕</button>
        </header>

        <ul v-if="notifications.length" class="space-y-3">
          <li
            v-for="n in notifications"
            :key="n.id"
            class="p-3 rounded-lg border flex justify-between items-center"
            :class="{
              'border-green-500 text-green-700 bg-green-50': n.type === 'success',
              'border-red-500 text-red-700 bg-red-50': n.type === 'error',
              'border-sky-500 text-sky-700 bg-sky-50': n.type === 'info',
            }"
          >
            <div>
              <p>{{ n.message }}</p>
            </div>
            <button @click="removeNotification(n.id)" class="text-gray-500 hover:text-black">✕</button>
          </li>
        </ul>

        <p v-else class="text-center text-gray-500 py-10">No notifications 🎉</p>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useNotificationStore } from '../stores/NotificationStore'

const { notifications, removeNotification } = useNotificationStore()
const isOpen = ref(false)

// Open/close modal
const openModal = () => { isOpen.value = true }
const closeModal = () => { isOpen.value = false }

defineExpose({ openModal })
</script>
