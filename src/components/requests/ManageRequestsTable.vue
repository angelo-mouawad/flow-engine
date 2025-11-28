<script setup lang="ts">
import type { RequestSubmission } from "../../types";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import RequestService from "../../services/RequestService";
import { useThemeStore } from "../../stores/themeStore";

const loading = ref(false);
const requests = ref<RequestSubmission[]>([]);
const error = ref<string | null>(null);
const tableKeys = [
    'Request Type',
    'Submitted By',
    'Start Date',
    'End Date',
    'Reason'
] as const;

type SortKey = typeof tableKeys[number];

const sortKey = ref<SortKey>('Start Date');

const sortOrder = ref<'asc' | 'desc'>('asc');

const sortFlowInstances = (key: SortKey) => {
  sortOrder.value = sortKey.value === key && sortOrder.value === 'asc' ? 'desc' : 'asc';
  sortKey.value = key;
  const compare = (a: string | number, b: string | number) => (a < b ? -1 : a > b ? 1 : 0);
  requests.value.sort((a, b) => {
    let res = 0;
    if (key === 'Request Type') res = compare(a.requestType.toLowerCase(), b.requestType.toLowerCase());
    else if (key === 'Submitted By') res = compare(a.data.allFields.submittedBy, b.data.allFields.submittedBy.toLowerCase());
    else if (key === 'Start Date') res = new Date(a.data.allFields.startDate).getTime() - new Date(b.data.allFields.startDate).getTime();
    else if (key === 'End Date') res = new Date(a.data.allFields.endDate).getTime() - new Date(b.data.allFields.endDate).getTime();
    else if (key === 'Reason') res = compare(a.data.allFields.reason.toLowerCase(), b.data.allFields.reason.toLowerCase());
    return sortOrder.value === 'asc' ? res : -res;
  });
};

const fetchRequests = async () => {
  loading.value = true;
  error.value = null;
  try {
    requests.value = await RequestService.getRequests();
    sortFlowInstances(sortKey.value);
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error fetching requests';
  } finally {
    loading.value = false;
  }
};

onMounted(fetchRequests);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
const router = useRouter();

const openRequestDetails = (id: string) => {
  router.push({ name: "RequestDetails", params: { id } });
};
</script>

<template>
  <table class="w-full border-collapse text-sm transition-colors duration-200" :class="isDarkMode ? 'text-white' : 'text-gray-900'">
    <thead>
      <tr class="text-left text-white bg-[#111]">
        <th v-for="key in tableKeys" 
            :key="key" 
            class="px-4 py-2 font-medium select-none cursor-pointer" 
            @click="sortFlowInstances(key)">
          {{ key }}
          <span class="ml-2 inline-block w-[1.5em] text-left">
            <span v-if="sortKey === key">
              {{ sortOrder === 'asc' ? '▲' : '▼' }}
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
      </tr>
    </thead>
    <tbody class="divide-y" :class="isDarkMode ? 'divide-[#2c2f31]' : 'divide-gray-200'">
      <tr v-if="loading" class="text-center">
        <td colspan="5" class="py-8" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">Loading requests...</td>
      </tr>
      <tr v-else-if="error" class="text-center border" :class="isDarkMode ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200' : 'bg-red-50 border-red-200 text-red-700'">
        <td colspan="5" class="px-4 py-3">{{ error }}</td>
      </tr>
      <tr v-else v-for="r in requests" :key="r.id" 
          @click="openRequestDetails(r.id)" 
          class="cursor-pointer transition-colors"
          :class="isDarkMode ? 'hover:bg-[#242628]' : 'hover:bg-gray-100'">
        <td class="pl-4 py-2 font-medium">{{ r.requestType }}</td>
        <td class="pl-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">{{ r.data.allFields.submittedBy }}</td>
        <td class="pl-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">{{ r.data.allFields.startDate }}</td>
        <td class="pl-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">{{ r.data.allFields.endDate }}</td>
        <td class="pl-4 py-2" :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">{{ r.data.allFields.reason }}</td>
      </tr>
    </tbody>
  </table>
</template>
