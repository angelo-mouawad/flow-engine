<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import ProcessComponent from "../main/Process.vue";
import FlowDiagram from "./FlowDiagram.vue";
import ProcessService from "../../services/ProcessService";
import type { Process } from "../../types";
import { useThemeStore } from "../../stores/themeStore";

const router = useRouter();

const flowName = ref("");
const flowNameError = ref("");
const stepsError = ref("");
const description = ref("");
const flowNameError = ref("");
const stepsError = ref("");

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

const themeStore = useThemeStore();
const isDarkMode = computed(() => themeStore.isDarkMode);

const labelTextColor = computed(() =>
  isDarkMode.value ? "text-gray-200" : "text-gray-700"
);

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
  <div
    :class="[
      'min-h-screen py-8 transition-colors duration-300',
      isDarkMode ? 'bg-[#1c1e1f] text-gray-100' : 'bg-gray-100 text-gray-900'
    ]"
  >
    <div class="w-full max-w-4xl mx-auto px-4">
      <div
        class="rounded-md border p-5 shadow-sm transition-colors duration-300"
        :class="isDarkMode ? 'bg-[#1c1e1f] border-[#2c2f31] text-white' : 'bg-white border-gray-300 text-gray-900'"
      >
        <div class="flex flex-col gap-4 mb-6">
          <div
            class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between"
          >
            <h2
              class="font-bold text-4xl pt-2"
              :class="isDarkMode ? 'text-gray-100' : 'text-gray-900'"
            >
              Create New Flow Definition
            </h2>
          </div>

          <button
            @click="goBackToFlowDef"
            class="flex items-center w-fit mb-2 transition-colors font-medium mt-2 cursor-pointer"
            :class="
              isDarkMode
                ? 'text-gray-200 hover:text-white'
                : 'text-yellow-700 hover:text-yellow-500'
            "
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
        </div>

        <p
          class="font-bold"
          :class="isDarkMode ? 'text-gray-100' : 'text-gray-900'"
        >
          Basic Information
        </p>
        <hr
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
          class="mb-2"
        />
        <form class="mt-5 flex flex-direction flex-wrap space-y-4">
          <div class="mb-4 grid grid-cols-2 gap-4 w-full">
            <div>
              <label
                class="block text-sm font-bold mb-2"
                :class="labelTextColor"
              >
                Flow Name
              </label>
              <input
                v-model="flowName"
                type="text"
                placeholder="Enter flow name"
                :class="[
                  'shadow border rounded w-full py-2 px-3 transition-colors duration-200 placeholder-gray-400',
                isDarkMode
                  ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                  : 'bg-white border-gray-300 text-gray-700',
                  flowNameError ? 'border-red-500 focus:border-red-500' : ''
                ]"
              />
              <p v-if="flowNameError" class="text-red-600 text-sm mt-1">
                {{ flowNameError }}
              </p>
            </div>
          </div>

          <div class="mb-4 w-full">
            <label
              class="block text-sm font-bold mb-2"
              :class="labelTextColor"
            >
              Description
            </label>
            <textarea
              v-model="description"
              rows="4"
              placeholder="Describe what this flow does"
              :class="[
                'shadow border rounded w-full py-2 px-3 transition-colors duration-200 placeholder-gray-400',
                isDarkMode
                  ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                  : 'bg-white border-gray-300 text-gray-700'
              ]"
            ></textarea>
          </div>
        </form>

        <p
          class="font-bold"
          :class="isDarkMode ? 'text-gray-100' : 'text-gray-900'"
        >
          Flow Steps
        </p>
        <hr
          class="pb-5"
          v-if="steps.length"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
        />
        <p v-if="stepsError" class="text-red-600 text-sm mt-1">
          {{ stepsError }}
        </p>

        <div v-if="steps.length > 0">
          <div
            v-for="step in steps"
            :key="step.id"
            class="rounded-md border p-5 mb-4 transition-colors duration-200"
            :class="
              isDarkMode
                ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                : 'bg-[#f5f5f5] border-gray-300 text-gray-800'
            "
          >
            <div class="flex justify-between items-center mb-4">
              <p class="font-bold">
                {{ step.processType }} PROCESS
              </p>

              <button
                @click="deleteStep(step.id)"
                type="button"
                class="flex items-center gap-1 font-semibold text-sm transition-colors"
                :class="
                  isDarkMode
                    ? 'text-red-300 hover:text-red-200'
                    : 'text-red-500 hover:text-red-700'
                "
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
            class="w-full rounded-lg border-2 border-dashed py-4 text-center mt-2 transition-colors"
            :class="
              isDarkMode
                ? 'border-gray-600 text-gray-200 hover:border-gray-400 hover:bg-[#242628]'
                : 'border-gray-300 text-gray-600 hover:border-yellow-500 hover:bg-yellow-50'
            "
          >
            <span class="font-semibold"> + Add Process </span>
          </button>

          <div
            v-if="showProcessMenu"
            class="absolute z-10 mt-2 w-full border rounded-md shadow-lg"
            :class="
              isDarkMode
                ? 'bg-[#1c1e1f] border-[#2c2f31]'
                : 'bg-white border-gray-300'
            "
          >
            <button
              v-for="type in [...new Set(allProcesses.map((p) => p.processType))]"
              :key="type"
              @click="addProcess(type)"
              class="block w-full text-left px-4 py-2 transition-colors"
              :class="
                isDarkMode
                  ? 'text-gray-200 hover:bg-[#242628]'
                  : 'text-gray-700 hover:bg-yellow-100'
              "
            >
              {{ type }} PROCESS
            </button>
          </div>

          <div v-if="steps.length > 0" class="mt-8 mb-6">
            <p
              class="font-bold mb-3"
              :class="isDarkMode ? 'text-gray-100' : 'text-gray-900'"
            >
              Flow Preview
            </p>
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

        <hr
          class="mt-5"
          :class="isDarkMode ? 'border-[#2c2f31]' : 'border-gray-200'"
        />

        <div class="p-5 flex justify-center gap-[2.6rem] mt-2 flex-wrap">
          <button
            @click="goBackToFlowDef"
            type="button"
            class="font-medium py-2 px-6 border rounded-md shadow-sm transition-colors"
            :class="
              isDarkMode
                ? 'bg-[#1c1e1f] border-[#2c2f31] text-white hover:bg-[#242628]'
                : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-100'
            "
          >
            Cancel
          </button>

          <button
            type="button"
            class="font-medium py-2 px-6 border rounded-md shadow-sm transition-colors"
            :class="
              isDarkMode
                ? 'bg-[#242628] border-[#2c2f31] text-white hover:bg-[#2f3234]'
                : 'bg-gray-200 border-gray-300 text-gray-700 hover:bg-gray-300'
            "
          >
            Save as Draft
          </button>

          <button
            @click="saveFlow"
            type="button"
            class="font-semibold py-2 px-6 border rounded-md shadow-sm transition-colors"
            :class="
              isDarkMode
                ? 'bg-yellow-500 border-yellow-600 text-gray-900 hover:bg-yellow-400'
                : 'bg-yellow-500 border-yellow-600 text-gray-900 hover:bg-yellow-600'
            "
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
