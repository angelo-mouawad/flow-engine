<script setup lang="ts">
import { computed, ref } from 'vue'
import ManageRequestsTable from './ManageRequestsTable.vue'
import RequestService from '../../services/RequestService'
import { useThemeStore } from '../../stores/themeStore'

const isClicked = ref(false)
const selectedRequest = ref<any | null>(null)
const tableRef = ref<any | null>(null)

const handleOpenRequest = (req: any) => {
  selectedRequest.value = req
  isClicked.value = true
}

const handleApproveRequest = async () => {
  if (selectedRequest.value) {
    await RequestService.approveRequest(selectedRequest.value.id);
    isClicked.value = false;
    selectedRequest.value = null;
    setTimeout(() => tableRef.value?.fetchRequests?.(), 500);
  }
};

const handleDeclineRequest = async () => {
  if (selectedRequest.value) {
    await RequestService.declineRequest(selectedRequest.value.id);
    isClicked.value = false;
    selectedRequest.value = null;
    setTimeout(() => tableRef.value?.fetchRequests?.(), 500);
  }
};

const closeModal = () => {
  isClicked.value = false
  selectedRequest.value = null
}

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
</script>

<template>
  <div
    v-if="isClicked"
    class="absolute items-center left-1/2 top-1/2 w-[80%] h-[70%] -translate-x-1/2 -translate-y-1/2 rounded-md border shadow-lg ring-1 ring-gray-200 z-50 transition-colors duration-200"
    :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-300 text-gray-900'"
  >
    <div class="p-6 h-full overflow-y-auto relative">
      <button
        @click="closeModal"
        class="absolute right-4 top-4 rounded-md cursor-pointer transition-colors"
        :class="isDarkMode ? 'text-gray-300 hover:text-white' : 'text-gray-600 hover:text-gray-900'"
      >
        ✕
      </button>
      <h2 class="text-2xl font-semibold mb-4 text-center">Manage this request</h2>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div
          v-for="(label, key) in {
            'Request Type': selectedRequest?.requestType,
            'Submitted By': selectedRequest?.data?.allFields?.submittedBy,
            'Start Date': selectedRequest?.data?.allFields?.startDate,
            'End Date': selectedRequest?.data?.allFields?.endDate
          }"
          :key="key"
          class="rounded-md p-4 border transition-colors duration-200"
          :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
        >
          <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">{{ key }}</div>
          <div class="mt-1 text-lg font-semibold" :class="isDarkMode ? 'text-white' : 'text-gray-800'">
            {{ label || 'no value' }}
          </div>
        </div>
      </div>
      <div class="w-full pt-4 flex justify-center">
        <div
          class="sm:col-span-2 rounded-md p-4 w-full sm:w-[60%] border transition-colors duration-200"
          :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-gray-50 border-gray-200'"
        >
          <div class="text-xs font-medium" :class="isDarkMode ? 'text-gray-400' : 'text-gray-500'">Reason</div>
          <div class="mt-1 text-lg font-semibold" :class="isDarkMode ? 'text-white' : 'text-gray-800'">
            {{ selectedRequest?.data?.allFields?.reason || 'no value' }}
          </div>
        </div>
      </div>
      <div class="bottom-5 flex justify-center gap-5">
        <button
          @click="handleApproveRequest"
          class="mt-6 px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-700"
        >
          Approve
        </button>
        <button
          @click="handleDeclineRequest"
          class="mt-6 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
        >
          Decline
        </button>
      </div>
    </div>
  </div>
  <div v-if="isClicked" class="fixed inset-0 bg-black opacity-50 z-40"></div>
  <div
    class="w-full rounded-md p-5 transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <div
      class="rounded-lg border p-6 shadow-sm transition-colors duration-300"
      :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31]' : 'bg-white border-gray-300'"
    >
      <h2 class="font-bold text-4xl py-5">Pending Requests:</h2>
      <div class="flex items-center justify-between mb-3 gap-4">
        <div class="relative flex-1 max-w-md">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Search"
            class="border rounded-md pl-9 px-3 py-1 text-sm focus:outline-none transition-colors w-full"
            :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white placeholder-gray-500' : 'bg-white border-gray-300 text-gray-800 placeholder-gray-400'"
          />
        </div>
      </div>

      <div class="overflow-x-auto rounded-md shadow-sm border transition-colors duration-300" :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'">
        <ManageRequestsTable ref="tableRef" @open-request="handleOpenRequest" />
      </div>
    </div>
  </div>
</template>
