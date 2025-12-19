<script setup lang="ts">
import type { FlowInstance, RequestSubmission } from "../../types";
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";
import RequestService from "../../services/RequestService";
import { useThemeStore } from "../../stores/themeStore";
import { useUserStore } from "../../stores/userStore";
import FlowInstanceService from "../../services/FlowInstanceService";

const userStore = useUserStore();

const currentUserEmail = computed(() =>
  userStore.currentUser?.email ??
  JSON.parse(sessionStorage.getItem("currentUser") || "{}")?.email ??
  null
);

window.addEventListener("storage", (event) => {
  if (event.key === "currentUser" && event.newValue) {
    const updatedUser = JSON.parse(event.newValue);
    userStore.currentUser = updatedUser;
  }
});

watch(currentUserEmail, (newEmail, oldEmail) => {
  if (newEmail && newEmail !== oldEmail) {
    fetchRequests();
  }
});

const props = defineProps<{ searchQuery: string }>();

const loading = ref(false);
const requests = ref<RequestSubmission[]>([]);
const error = ref<string | null>(null);
const flowInstancesById = ref<Record<string, FlowInstance>>({});
const tableKeys = [
  "Request Type",
  "Submitted By",
  "Submitted At",
  "Flow Instance Name"
] as const;

type SortKey = (typeof tableKeys)[number];

const sortKey = ref<SortKey>("Submitted At");

const sortOrder = ref<"asc" | "desc">("asc");

const sortRequests = (key: SortKey) => {
  sortOrder.value =
    sortKey.value === key && sortOrder.value === "asc" ? "desc" : "asc";
  sortKey.value = key;
  const compare = (a: string | number, b: string | number) =>
    a < b ? -1 : a > b ? 1 : 0;
  requests.value.sort((a, b) => {
    let res = 0;
    if (key === "Request Type")
      res = compare(a.requestTypeName.toLowerCase(), b.requestTypeName.toLowerCase());
    else if (key === "Submitted By")
      res = compare(
        a.data.allFields.submittedBy.toLowerCase(),
        b.data.allFields.submittedBy.toLowerCase()
      );
    else if (key === "Submitted At")
      res = new Date(a.submittedAt).getTime() - new Date(b.submittedAt).getTime();
    else if (key === "Flow Instance Name")
      res = compare(
        (flowInstancesById.value[a.flowInstanceId]?.title || "").toLowerCase(),
        (flowInstancesById.value[b.flowInstanceId]?.title || "").toLowerCase()
      );
    return sortOrder.value === "asc" ? res : -res;
  });
};

const fetchRequests = async () => {
  loading.value = true;
  error.value = null;
  try {
    const reqs = await RequestService.getRequests();
    requests.value = Array.isArray(reqs)
      ? reqs.map(r => ({
          ...r,
          submittedAt: r.submittedAt ? new Date(r.submittedAt) : new Date(),
          processedAt: r.processedAt ? new Date(r.processedAt) : new Date(),
        }))
      : [];
    sortRequests(sortKey.value);

    // Fetch related flow instances so the table can display titles
    await fetchFlowInstances();
  } catch (e) {
    error.value = e instanceof Error ? e.message : "Error fetching requests";
  } finally {
    loading.value = false;
  }
};

const fetchFlowInstances = async () => {
  try {
    const ids = Array.from(new Set(requests.value.map(r => r.flowInstanceId)));
    const flowInstances = await Promise.all(ids.map((id) => FlowInstanceService.getFlowInstanceById(id)));

    const nextMap: Record<string, FlowInstance> = {};
    for (const instance of flowInstances) {
      nextMap[instance.id] = instance;
    }
    flowInstancesById.value = nextMap;

    return flowInstances;
  } catch (e) {
    console.error("Error fetching flow instances:", e);
    flowInstancesById.value = {};
    return [];
  }
};

onMounted(fetchRequests);

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);
const router = useRouter();

const openRequestDetails = (id: string) => {
  router.push({ name: "RequestDetails", params: { id } });
};

const filteredRequests = computed(() => {
  if (!props.searchQuery.trim()) {
    return requests.value;
  }

  const search = props.searchQuery.toLowerCase().trim();

  return requests.value.filter((req) => {
    const reqTypeMatch = req.requestTypeName.toLowerCase().includes(search);

    const searchableData = JSON.stringify(Object.values(req.data.allFields))
      .toLowerCase()
      .replace(/"id":\s*"[^"]*"/gi, "")
      .replace(/"[a-f0-9-]{36}"/gi, "")
      .replace(/\bid\b:\s*"[^"]*"/gi, "");

    const dataMatch = searchableData.includes(search);
    return reqTypeMatch || dataMatch;
  });
});
</script>

<template>
  <table
    class="w-full border-collapse text-sm transition-colors duration-200"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <thead>
      <tr class="text-left text-white bg-[#111]">
        <th
          v-for="key in tableKeys"
          :key="key"
          class="px-4 py-2 font-medium select-none cursor-pointer"
          @click="sortRequests(key)"
        >
          {{ key }}
          <span class="ml-2 inline-block w-[1.5em] text-left">
            <span v-if="sortKey === key">
              {{ sortOrder === "asc" ? "▲" : "▼" }}
            </span>
            <span v-else class="text-gray-400">▲▼</span>
          </span>
        </th>
      </tr>
    </thead>
    <tbody
      class="divide-y"
      :class="isDarkMode ? 'divide-[#2c2f31]' : 'divide-gray-200'"
    >
      <tr v-if="loading" class="text-center">
        <td
          colspan="5"
          class="py-8"
          :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
        >
          Loading requests...
        </td>
      </tr>
      <tr
        v-else-if="error"
        class="text-center border"
        :class="
          isDarkMode
            ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200'
            : 'bg-red-50 border-red-200 text-red-700'
        "
      >
        <td colspan="5" class="px-4 py-3">{{ error }}</td>
      </tr>
      <tr v-else-if="filteredRequests.length === 0">
        <td
          colspan="5"
          class="text-center py-8"
          :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'"
        >
          No requests found.
        </td>
      </tr>
      <tr
        v-else
        v-for="r in filteredRequests"
        :key="r.id"
        @click="openRequestDetails(r.id)"
        class="cursor-pointer transition-colors"
        :class="isDarkMode ? 'hover:bg-[#242628]' : 'hover:bg-gray-100'"
      >
        <td class="pl-4 py-2 font-medium">{{ r.requestTypeName }}</td>
        <td
          class="pl-4 py-2"
          :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
        >
          {{ r.data.allFields.submittedBy }}
        </td>
        <td
          class="pl-4 py-2"
          :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
        >
          {{ r.submittedAt.toLocaleDateString() }} {{ r.submittedAt.toLocaleTimeString() }}
        </td>
        <td
          class="pl-4 py-2"
          :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
        >
          {{ flowInstancesById[r.flowInstanceId]?.title || 'No Name Found' }}
        </td>
      </tr>
    </tbody>
  </table>
</template>
