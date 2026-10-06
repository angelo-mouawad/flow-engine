<script setup lang="ts">
import type {
  FlowDefinition,
  NotificationType,
  RequestType,
} from "../../types";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import deleteIcon from "/images/delete1.png.webp";
import { useThemeStore } from "../../stores/themeStore";

const props = withDefaults(
  defineProps<{
    searchQuery: string;
    filters?: Record<string, boolean>;
  }>(),
  {
    filters: () => ({}),
  }
);

const loading = ref(false);
const flowDefinitions = ref<FlowDefinition[]>([]);
const error = ref<string | null>(null);

const sortKey = ref<"title" | "updatedAt">("updatedAt");
const sortOrder = ref<"asc" | "desc">("asc");

const sortFlowDefinitions = (key: "title" | "updatedAt") => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "asc";
  }
  flowDefinitions.value = [...flowDefinitions.value].sort((a, b) => {
    let result = 0;
    if (key === "title") {
      if (a.title.toLowerCase() < b.title.toLowerCase()) result = -1;
      if (a.title.toLowerCase() > b.title.toLowerCase()) result = 1;
    } else if (key === "updatedAt" && a.updatedAt && b.updatedAt) {
      result =
        new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime();
    }
    return sortOrder.value === "asc" ? result : -result;
  });
};

const fetchFlowDefinitions = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await FlowDefinitionService.getFlowDefinitions();
    flowDefinitions.value = data;
    sortFlowDefinitions(sortKey.value);
  } catch (e) {
    error.value =
      e instanceof Error
        ? e.message
        : "An error occurred while fetching flow definitions";
    console.error("Failed to fetch flow definitions:", e);
  } finally {
    loading.value = false;
  }
};

const onRowIconClick = async (def: FlowDefinition) => {
  try {
    if (def.id === undefined) {
      throw new Error("Flow definition has no ID");
    }
    await FlowDefinitionService.deleteFlowDefinition(def.id);
    flowDefinitions.value = flowDefinitions.value.filter(
      (d) => d.id !== def.id
    );
  } catch (e) {
    error.value =
      e instanceof Error ? e.message : "Failed to delete flow definition";
    console.error("Failed to delete flow definition:", e);
  }
};

onMounted(() => {
  fetchFlowDefinitions();
});

const router = useRouter();
const goToDetails = (id: string) => {
  router.push({ name: "FlowDefinitionDetails", params: { id } });
};

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const selectedFilters = computed(() => props.filters || {});
const hasActiveFilters = computed(() =>
  Object.values(selectedFilters.value).some(Boolean)
);

const hasProcessType = (
  def: FlowDefinition,
  processType: "REQUEST" | "NOTIFICATION" | "APPROVAL"
) => {
  return (def.processes ?? []).some(
    (process) => process.processType === processType
  );
};

const hasRequestType = (def: FlowDefinition, type: RequestType) => {
  return (def.processes ?? []).some(
    (process) =>
      process.processType === "REQUEST" && process.requestTypeName === type
  );
};

const hasNotificationType = (def: FlowDefinition, type: NotificationType) => {
  return (def.processes ?? []).some(
    (process) =>
      process.processType === "NOTIFICATION" &&
      process.notificationType === type
  );
};

const parentFilterKeys = ["request", "notification", "approval"] as const;

const matchesSelectedFilters = (def: FlowDefinition) => {
  if (!hasActiveFilters.value) {
    return true;
  }

  const currentFilters = selectedFilters.value;

  return parentFilterKeys.every((key) => {
    if (key === "approval") {
      if (!currentFilters.approval) {
        return true;
      }
      return hasProcessType(def, "APPROVAL");
    }

    if (key === "request") {
      const childOptions = [
        { key: "request-absence", type: "ABSENCE_REQUEST" as RequestType },
        { key: "request-clock-in", type: "CLOCKIN_REQUEST" as RequestType },
        { key: "request-meeting", type: "MEETING_REQUEST" as RequestType },
        { key: "request-task-change", type: "TASK_CHANGE_REQUEST" as RequestType }
      ];
      const activeChildren = childOptions.filter(
        (child) => currentFilters[child.key]
      );
      const shouldFilter =
        currentFilters.request || activeChildren.length > 0;
      if (!shouldFilter) {
        return true;
      }
      if (activeChildren.length === 0) {
        return hasProcessType(def, "REQUEST");
      }
      return activeChildren.some((child) => hasRequestType(def, child.type));
    }

    if (key === "notification") {
      const childOptions = [
        {
          key: "notification-email",
          type: "EMAIL_NOTIFICATION" as NotificationType,
        },
        {
          key: "notification-pop-up",
          type: "POPUP_NOTIFICATION" as NotificationType,
        },
      ];
      const activeChildren = childOptions.filter(
        (child) => currentFilters[child.key]
      );
      const shouldFilter =
        currentFilters.notification || activeChildren.length > 0;
      if (!shouldFilter) {
        return true;
      }
      if (activeChildren.length === 0) {
        return hasProcessType(def, "NOTIFICATION");
      }
      return activeChildren.some((child) =>
        hasNotificationType(def, child.type)
      );
    }

    return true;
  });
};

const filteredFlowDefinitions = computed(() => {
  const trimmedQuery = props.searchQuery.trim().toLowerCase();

  const baseList = trimmedQuery
    ? flowDefinitions.value.filter((def) => {
        const searchableData = JSON.stringify(Object.values(def))
          .toLowerCase()
          .replace(/"id":\s*"[^"]*"/gi, "")
          .replace(/"[a-f0-9-]{36}"/gi, "")
          .replace(/\bid\b:\s*"[^"]*"/gi, "");

        return searchableData.includes(trimmedQuery);
      })
    : flowDefinitions.value;

  if (!hasActiveFilters.value) {
    return baseList;
  }

  return baseList.filter((def) => matchesSelectedFilters(def));
});
</script>

<template>
  <div
    class="w-full transition-colors duration-300"
    :class="isDarkMode ? 'text-white' : 'text-gray-900'"
  >
    <table
      class="w-full border-collapse text-sm transition-colors duration-200"
      :class="isDarkMode ? 'text-white' : 'text-gray-900'"
    >
      <thead>
        <tr class="text-left text-white bg-[#111]">
          <th
            class="px-4 py-2 font-medium select-none cursor-pointer"
            @click="sortFlowDefinitions('title')"
          >
            Name
            <span class="ml-2">
              <span
                class="ml-2 inline-block"
                style="width: 1.5em; text-align: left"
              >
                <span v-if="sortKey === 'title'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class="px-4 py-2 font-medium" style="width: 40%">Description</th>
          <th
            class="px-4 py-2 font-medium select-none cursor-pointer"
            @click="sortFlowDefinitions('updatedAt')"
          >
            Last Updated
            <span class="ml-2">
              <span
                class="ml-2 inline-block"
                style="width: 1.5em; text-align: left"
              >
                <span v-if="sortKey === 'updatedAt'">
                  <span v-if="sortOrder === 'asc'">▲</span>
                  <span v-else>▼</span>
                </span>
                <span v-else class="text-gray-400">▲▼</span>
              </span>
            </span>
          </th>
          <th class="px-4 py-2"></th>
        </tr>
      </thead>
      <tbody
        class="divide-y"
        :class="isDarkMode ? 'divide-[#2c2f31]' : 'divide-gray-200'"
      >
        <tr v-if="loading">
          <td colspan="4" class="text-center py-8">
            <div :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'">
              Loading flow definitions...
            </div>
          </td>
        </tr>
        <tr v-else-if="error">
          <td colspan="4" class="p-4">
            <div
              class="px-4 py-3 rounded border"
              :class="
                isDarkMode
                  ? 'bg-[#2b1b1b] border-[#4c1d1d] text-red-200'
                  : 'bg-red-50 border-red-200 text-red-700'
              "
            >
              <p class="font-medium">Error</p>
              <p class="text-sm">{{ error }}</p>
            </div>
          </td>
        </tr>
        <tr v-else-if="filteredFlowDefinitions.length === 0">
          <td
            colspan="4"
            class="text-center py-8"
            :class="isDarkMode ? 'text-gray-300' : 'text-gray-500'"
          >
            No flow definitions found.
          </td>
        </tr>
        <tr
          v-else
          v-for="def in filteredFlowDefinitions"
          :key="def.id"
          class="cursor-pointer transition-colors"
          :class="isDarkMode ? 'hover:bg-[#242628]' : 'hover:bg-gray-50'"
          @click="goToDetails(def.id!)"
        >
          <td class="px-4 py-2 font-medium">{{ def.title }}</td>
          <td
            class="px-4 py-2 truncate"
            :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
            style="max-width: 320px"
          >
            {{ def.description || "—" }}
          </td>
          <td
            class="px-4 py-2"
            :class="isDarkMode ? 'text-gray-300' : 'text-gray-600'"
          >
            {{
              new Date(def.updatedAt ?? "Never").toLocaleDateString("en-GB", {
                year: "numeric",
                month: "long",
                day: "numeric",
              })
            }}
          </td>
          <td class="px-4 py-2 text-right min-w-[72px]">
            <button
              @click.stop="onRowIconClick(def)"
              class="inline-flex items-center p-1 rounded focus:ring-2 focus:ring-blue-400 transition-colors"
              :class="isDarkMode ? 'hover:bg-[#1c1e1f]' : 'hover:bg-gray-100'"
              :aria-label="`Delete ${def.title}`"
            >
              <img :src="deleteIcon" alt="Delete" class="w-5 h-5" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
