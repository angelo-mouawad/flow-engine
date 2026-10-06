<script setup lang="ts">
import { computed, ref } from "vue";
import { useNotificationStore } from "../../stores/notificationStore";
import { useThemeStore } from "../../stores/themeStore";
import { useRouter } from "vue-router";

const router = useRouter();
const { notifications, removeNotification } = useNotificationStore();
const isOpen = ref(false);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const isApprovalRequest = (message: string) => {
  return message.toLowerCase().includes('awaiting approval');
}

const goToRequests = () => {
  closeModal();
  router.push('/pending-requests');
}

const notificationTypeClasses = computed(() =>
  isDarkMode.value
    ? {
        success: "border-green-500/40 bg-green-500/10 text-green-200",
        error: "border-red-500/40 bg-red-500/10 text-red-200",
        info: "border-blue-500/40 bg-blue-500/10 text-blue-200",
      }
    : {
        success: "border-green-200 bg-green-50 text-green-800",
        error: "border-red-200 bg-red-50 text-red-800",
        info: "border-blue-100 bg-blue-50 text-blue-800",
      }
);

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
    <Transition name="fade">
      <div
        v-if="isOpen"
        class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-start justify-center z-[10000] p-4 pt-24"
        @click.self="closeModal"
      >
        <div
          class="rounded-2xl w-full max-w-lg max-h-[75vh] flex flex-col shadow-2xl border transition-all duration-300"
          :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-100'"
        >
          <header class="flex justify-between items-center p-5 border-b" :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-100'">
            <div>
              <h2 class="text-xl font-bold" :class="isDarkMode ? 'text-white' : 'text-gray-800'">Notifications</h2>
              <p class="text-xs font-medium opacity-60">Manage your recent activity</p>
            </div>
            <button @click="closeModal" class="p-2 rounded-full hover:bg-gray-500/10 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </header>

          <div class="overflow-y-auto p-4 space-y-4">
            <div v-if="notifications.length" class="space-y-3">
              <div
                v-for="n in notifications"
                :key="n.id"
                class="group relative p-4 rounded-xl border transition-all"
                :class="notificationTypeClasses[n.type]"
              >
                <div class="flex gap-3">
                  <div class="mt-1">
                    <span v-if="n.type === 'success'">✅</span>
                    <span v-else-if="n.type === 'error'">❌</span>
                    <span v-else>ℹ️</span>
                  </div>

                  <div class="flex-1">
                    <p class="text-sm leading-relaxed mb-2">{{ n.message }}</p>
                    
                    <button 
                      v-if="isApprovalRequest(n.message)"
                      @click="goToRequests()"
                      class="text-xs font-bold underline decoration-2 underline-offset-4 hover:opacity-70 transition-opacity"
                    >
                      Handle Request &rarr;
                    </button>

                    <div class="text-[10px] uppercase tracking-wider opacity-50 mt-1">
                      {{ new Date(n.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }}
                    </div>
                  </div>

                  <button
                    @click="removeNotification(n.id)"
                    class="opacity-0 group-hover:opacity-100 p-1 rounded-md hover:bg-black/5 transition-all"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-12">
              <div class="text-4xl mb-4">🔔</div>
              <p class="text-lg font-medium" :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'">All caught up!</p>
              <p class="text-sm opacity-60">No new notifications at the moment.</p>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-thumb { background: rgba(156, 163, 175, 0.3); border-radius: 10px; }
</style>
