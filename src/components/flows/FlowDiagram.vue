<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { VueFlow, useVueFlow, MarkerType } from "@vue-flow/core";
import type { Process } from "../../types";
import { useThemeStore } from "../../stores/themeStore";

const props = withDefaults(
  defineProps<{
    processes: Process[];
    interactive?: boolean;
  }>(),
  {
    interactive: true,
  }
);

// all nodes need XY position
const NODE_HEIGHT = 80;
const NODE_SPACING = 60;
const X_CENTER = 250; // nodes centered @ x=250
const START_Y = 50;

const nodes = computed(() => {
  const result: any[] = [];
  let yPosition = START_Y;

  props.processes.forEach((process, index) => {
    if (!process) {
      return;
    }

    const nodeId = process.id || `process-${index}`;
    const processType = process.processType;

    let label = process.name;

    if (!label || label.trim() === "") {
      if (process.processType === "REQUEST") {
        const requestTypeName = process.requestTypeName;
        if (requestTypeName === "ABSENCE_REQUEST") {
          label = "Absence Request";
        } else if (requestTypeName === "CLOCKIN_REQUEST") {
          label = "Clock-In Request";
        } else {
          label = "Request Process";
        }
      } else if (process.processType === "APPROVAL") {
        label = "Approval Process";
      } else if (process.processType === "NOTIFICATION") {
        const notificationType = process.notificationType;
        if (notificationType === "EMAIL_NOTIFICATION") {
          label = "Email Notification";
        } else if (notificationType === "POPUP_NOTIFICATION") {
          label = "Popup Notification";
        } else {
          label = "Notification Process";
        }
      } else {
        label = "Unnamed Process";
      }
    }

    const isFirst = index === 0;
    const isLast = index === props.processes.length - 1;

    result.push({
      id: nodeId,
      type: isFirst ? "input" : isLast ? "output" : "default",
      position: { x: X_CENTER, y: yPosition },
      data: {
        label: label,
        processType: processType,
      },
    });

    yPosition += NODE_HEIGHT + NODE_SPACING;
  });

  return result;
});

const edges = computed(() => {
  const result: any[] = [];

  for (let i = 0; i < props.processes.length - 1; i++) {
    const currentProcess = props.processes[i];
    const nextProcess = props.processes[i + 1];

    if (!currentProcess || !nextProcess) {
      continue;
    }

    const currentId = currentProcess.id || `process-${i}`; // it should change after backend processes update
    const nextId = nextProcess.id || `process-${i + 1}`;

    result.push({
      id: `e-${currentId}-${nextId}`,
      source: currentId,
      target: nextId,
      type: "smoothstep",
      markerEnd: { type: MarkerType.ArrowClosed, color: "#6b7280" },
    });
  }

  return result;
});

const vueFlowNodes = ref(nodes.value);
const vueFlowEdges = ref(edges.value);

const { fitView } = useVueFlow();

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

watch([nodes, edges], ([newNodes, newEdges]) => {
  vueFlowNodes.value = newNodes;
  vueFlowEdges.value = newEdges;

  if (props.interactive) {
    setTimeout(() => {
      fitView({ duration: 200 });
    }, 50);
  }
});
</script>

<template>
  <div
    class="flow-diagram-container"
    :class="{ 'flow-diagram-container--dark': isDarkMode }"
  >
    <VueFlow
      :nodes="vueFlowNodes"
      :edges="vueFlowEdges"
      :nodes-draggable="true"
      :zoom-on-scroll="interactive"
      :pan-on-scroll="interactive"
      :pan-on-drag="interactive"
      :zoom-on-double-click="interactive"
      fit-view-on-init
      class="flow-diagram"
    >
    </VueFlow>
  </div>
</template>

<style scoped>
.flow-diagram-container {
  width: 100%;
  height: 300px;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  background-color: #fafafa;
}

.flow-diagram-container--dark {
  background-color: #1c1e1f;
  border-color: #181a1b;
}

.flow-diagram {
  width: 100%;
  height: 100%;
}
</style>

<style>
@import "@vue-flow/core/dist/style.css";
@import "@vue-flow/core/dist/theme-default.css";

.vue-flow__node {
  padding: 14px 20px;
  border-radius: 0.375rem;
  border: 2px solid #d1d5db;
  background: white;
  font-weight: 600;
  color: #374151;
  min-width: 160px;
  text-align: center;
}

.vue-flow__node-input {
  background: #fef3c7;
  border-color: #fbbf24;
  color: #78350f;
}

.vue-flow__node-output {
  background: #f3f4f6;
  border-color: #9ca3af;
  color: #1f2937;
}

.vue-flow__node-default {
  background: white;
  border-color: #d1d5db;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.vue-flow__node:hover {
  border-color: #fbbf24;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.vue-flow__edge-path {
  stroke: #6b7280;
  stroke-width: 2;
}

.vue-flow__edge .vue-flow__edge-path {
  stroke: #6b7280;
}

.vue-flow__handle {
  opacity: 0;
  pointer-events: none;
}

.vue-flow__background {
  background-color: #fafafa;
}
</style>
