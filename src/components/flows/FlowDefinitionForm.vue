<script setup lang="ts">
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import ProcessComponent from "../main/Process.vue";
import FlowDiagram from "./FlowDiagram.vue";
import ProcessService from "../../services/ProcessService";
import type { Process } from "../../types";

const router = useRouter();

const flowName = ref("");
const flowNameError = ref("");
const stepsError = ref("");
const description = ref("");

const steps = ref<
  { id: string; processType: string; name: string; selectedProcessId?: string }[]
>([]);

let nextStepId = "1";

const showProcessMenu = ref(false);
const allProcesses = ref<Process[]>([]);

(async () => {
  allProcesses.value = await ProcessService.getProcess();
})();

const addProcess = (processType: string) => {
  steps.value.push({
    id: nextStepId,
    processType,
    name: "",
    selectedProcessId: undefined,
  });
  nextStepId = (Number(nextStepId) + 1).toString();
  showProcessMenu.value = false;
};

const deleteStep = (id: string) => {
  steps.value = steps.value.filter((s) => s.id !== id);
};

const updateStep = (stepId: string, updates: Partial<Process>) => {
  const step = steps.value.find((s) => s.id === stepId);
  if (!step) return;

  if (updates.id) {
    step.selectedProcessId = updates.id;

    const p = allProcesses.value.find((x) => x.id === updates.id);
    if (p) step.name = p.name;
  }
};

const saveFlow = async () => {
  if (!flowName.value.trim()) {
    flowNameError.value = "Flow name is required";
    return;
  }

  if (!steps.value.length) {
    stepsError.value = "At least one flow step is required";
    return;
  }

  const payload = {
    title: flowName.value,
    description: description.value,
    processes: steps.value
      .filter((s) => s.selectedProcessId)
      .map((s) => s.selectedProcessId!)
  };

  console.log("FINAL PAYLOAD:", payload);

  await FlowDefinitionService.addNewFlowDefinition(payload);
  router.push("/flow-definitions");
};

const goBackToFlowDef = () => router.back();

watch(flowName, (value) => {
  if (value.trim()) flowNameError.value = "";
});

watch(
  () => steps.value.length,
  (length) => {
    if (length) stepsError.value = "";
  }
);
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <div class="rounded-md border bg-white p-5 border-gray-300 shadow-sm">
      <h2 class="font-bold text-4xl pt-5">Create New Flow Definition</h2>

      <button
        @click="goBackToFlowDef"
        class="flex items-center text-yellow-700 hover:text-yellow-500 mb-6 transition-colors font-medium mt-2 cursor-pointer"
      >
        <svg
          class="w-4 h-4 mr-2 mt-0.5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="3"
            d="M15 19l-7-7 7-7"
          />
        </svg>
        <span>Return</span>
      </button>

      <p class="font-bold">Basic Information</p>
      <hr />
      <form class="mt-5 flex flex-direction flex-wrap space-y-4">
        <div class="mb-4 grid grid-cols-2 gap-4 w-full">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Flow Name
            </label>
            <input
              v-model="flowName"
              type="text"
              placeholder="Enter flow name"
              :class="[
                'shadow border rounded w-full py-2 px-3 text-gray-700',
                flowNameError ? 'border-red-500 focus:border-red-500' : ''
              ]"
            />
            <p v-if="flowNameError" class="text-red-600 text-sm mt-1">
              {{ flowNameError }}
            </p>
          </div>
        </div>

        <div class="mb-4 w-full">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Description
          </label>
          <textarea
            v-model="description"
            rows="4"
            placeholder="Describe what this flow does"
            class="shadow border rounded w-full py-2 px-3 text-gray-700"
          ></textarea>
        </div>
      </form>

      <p class="font-bold">Flow Steps</p>
      <hr class="pb-5" v-if="steps.length" />
      <p v-if="stepsError" class="text-red-600 text-sm mt-1">
        {{ stepsError }}
      </p>

      <div v-if="steps.length > 0">
        <div
          v-for="step in steps"
          :key="step.id"
          class="rounded-md border p-5 bg-[#f5f5f5] mb-4"
        >
          <div class="flex justify-between items-center mb-4">
            <p class="font-bold text-gray-700">
              {{ step.processType }} PROCESS
            </p>

            <button
              @click="deleteStep(step.id)"
              type="button"
              class="flex items-center gap-1 text-red-500 hover:text-red-700 font-semibold text-sm"
            >
              <svg
                class="w-4 h-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                />
              </svg>
              <span>Remove</span>
            </button>
          </div>

          <ProcessComponent
            :process="step"
            @update-process="(u) => updateStep(step.id, u)"
          />
        </div>
      </div>

      <div class="relative w-full">
        <button
          @click="showProcessMenu = !showProcessMenu"
          type="button"
          class="w-full rounded-lg border-2 border-dashed border-gray-300 py-4 text-center hover:border-yellow-500 hover:bg-yellow-50 mt-2"
        >
          <span class="font-semibold text-gray-600"> + Add Process </span>
        </button>

        <div
          v-if="showProcessMenu"
          class="absolute z-10 mt-2 w-full bg-white border border-gray-300 rounded-md shadow-lg"
        >
          <button
            v-for="type in [...new Set(allProcesses.map((p) => p.processType))]"
            :key="type"
            @click="addProcess(type)"
            class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-yellow-100"
          >
            {{ type }} PROCESS
          </button>
        </div>

        <div v-if="steps.length > 0" class="mt-8 mb-6">
          <p class="font-bold mb-3">Flow Preview</p>
          <FlowDiagram
            :processes="steps
              .filter((s) => s.selectedProcessId)
              .map((s) => ({
                id: String(s.selectedProcessId),
                processType: s.processType,
                name: s.name || s.processType,
              }))"
            :interactive="true"
          />
        </div>
      </div>

      <hr class="mt-5" />

      <div class="p-5 flex justify-center gap-[2.6rem] mt-2">
        <button
          @click="goBackToFlowDef"
          type="button"
          class="bg-white hover:bg-gray-100 text-gray-700 font-medium py-2 px-6 border border-gray-300 rounded-md shadow-sm"
        >
          Cancel
        </button>

        <button
          type="button"
          class="bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-6 border border-gray-300 rounded-md shadow-sm"
        >
          Save as Draft
        </button>

        <button
          @click="saveFlow"
          type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm"
        >
          Save
        </button>
      </div>
    </div>
  </div>
</template>
