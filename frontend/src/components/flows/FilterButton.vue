<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref } from "vue";

const props = withDefaults(
  defineProps<{
    panelTarget?: string;
  }>(),
  {
    panelTarget: "#flow-definitions-filter-panel",
  }
);

type FilterItem = {
  key: string;
  label: string;
  checked: boolean;
  expanded?: boolean;
  children?: Array<{
    key: string;
    label: string;
    checked: boolean;
  }>;
};

const emit = defineEmits<{
  (e: "change", payload: Record<string, boolean>): void;
  (e: "visibility-change", payload: boolean): void;
}>();

const dropdownVisible = ref(false);
const rootRef = ref<HTMLElement | null>(null);

const filterGroups = reactive<FilterItem[]>([
  {
    key: "request",
    label: "Request",
    checked: false,
    expanded: false,
    children: [
      { key: "absence", label: "Absence", checked: false },
      { key: "clock-in", label: "Clock In", checked: false },
      { key: "task-change", label: "Task Change", checked: false },
      { key: "meeting", label: "Meeting", checked: false }
    ],
  },
  {
    key: "notification",
    label: "Notification",
    checked: false,
    expanded: false,
    children: [
      { key: "email", label: "Email", checked: false },
      { key: "pop-up", label: "Pop Up", checked: false },
    ],
  },
  {
    key: "approval",
    label: "Approval",
    checked: false,
    expanded: true,
  },
]);

const paletteStyles = {
  backgroundColor: "var(--surface-bg)",
  color: "var(--text-color)",
  borderColor: "var(--surface-border)",
};

const cardStyles = {
  backgroundColor: "var(--surface-bg)",
  borderColor: "var(--surface-border)",
  color: "var(--text-color)",
};

const dividerStyles = {
  borderColor: "var(--surface-border)",
};

const emitSelection = () => {
  const selection: Record<string, boolean> = {};
  filterGroups.forEach((group) => {
    selection[group.key] = group.checked;
    group.children?.forEach((child) => {
      selection[`${group.key}-${child.key}`] = child.checked;
    });
  });
  emit("change", selection);
};

const toggleParent = (group: FilterItem) => {
  group.checked = !group.checked;

  if (group.checked && group.children) {
    group.expanded = true;
  }

  if (!group.checked && group.children) {
    group.expanded = false;
    group.children.forEach((child) => {
      child.checked = false;
    });
  }

  emitSelection();
};

const toggleChild = (group: FilterItem, childKey: string) => {
  const child = group.children?.find((item) => item.key === childKey);
  if (!child) return;

  child.checked = !child.checked;

  if (child.checked) {
    group.checked = true;
    group.expanded = true;
  }

  emitSelection();
};

const clearGroup = (group: FilterItem) => {
  group.checked = false;
  group.expanded = false;
  group.children?.forEach((child) => {
    child.checked = false;
  });
  emitSelection();
};

const setVisibility = (value: boolean) => {
  dropdownVisible.value = value;
  emit("visibility-change", value);
};

const closeDropdown = () => {
  if (!dropdownVisible.value) return;
  setVisibility(false);
};

const handleClickOutside = (event: MouseEvent) => {
  if (!dropdownVisible.value) return;
  const target = event.target as Node;
  if (rootRef.value?.contains(target)) return;
  const panelEl = document.querySelector(props.panelTarget);
  if (panelEl && panelEl.contains(target)) return;
  closeDropdown();
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});

const toggleDropdown = () => {
  setVisibility(!dropdownVisible.value);
};
</script>

<template>
  <div ref="rootRef" class="relative inline-flex items-center">
    <button
      type="button"
      class="flex items-center gap-2 rounded-md border px-4 py-1.5 text-sm font-medium transition-colors duration-200 cursor-pointer focus:outline-none shadow-sm"
      :style="paletteStyles"
      @click="toggleDropdown"
      @keydown.escape.stop.prevent="closeDropdown"
      :aria-expanded="dropdownVisible"
      aria-haspopup="true"
    >
      <svg
        class="h-4 w-4"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M3 4h18M5 12h14M8 20h8"
        />
      </svg>
      <span>Filter</span>
    </button>

    <Teleport v-if="dropdownVisible" :to="props.panelTarget">
      <div
        class="w-[220px] rounded-md border transition-colors duration-200"
        :style="paletteStyles"
        @keydown.escape.stop.prevent="closeDropdown"
      >
        <div class="flex flex-col gap-1.5 p-3 text-sm">
          <div
            v-for="group in filterGroups"
            :key="group.key"
            class="rounded-md border"
            :style="cardStyles"
          >
            <div
              class="flex items-center justify-between border-b px-3 py-2 text-sm font-semibold"
              :style="dividerStyles"
            >
              <label class="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  class="cursor-pointer"
                  :checked="group.checked"
                  @change="toggleParent(group)"
                />
                <span>{{ group.label }}</span>
              </label>
              <button
                type="button"
                class="text-xs font-medium cursor-pointer underline-offset-2"
                :style="{ color: 'var(--text-color)' }"
                @click="clearGroup(group)"
              >
                Clear
              </button>
            </div>

            <div
              v-if="group.children && group.expanded"
              class="flex flex-col gap-1.5 px-3 py-2 pl-4"
            >
              <label
                v-for="child in group.children"
                :key="child.key"
                class="flex items-center gap-2 cursor-pointer font-normal"
              >
                <input
                  type="checkbox"
                  class="cursor-pointer"
                  :checked="child.checked"
                  @change="toggleChild(group, child.key)"
                />
                <span>{{ child.label }}</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
