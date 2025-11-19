<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import Process from "../main/Process.vue";
import FlowDiagram from "./FlowDiagram.vue";

const router = useRouter();

const flowName = ref("");
const description = ref("");
const steps = ref<any[]>([]);
const showProcessMenu = ref(false);

let nextStepId = "1";

const addProcess = (type: string) => {
  steps.value.push({ id: nextStepId, type, subtype: null });
  nextStepId = (Number(nextStepId) + 1).toString();
  showProcessMenu.value = false;
};

const deleteStep = (id: string) => {
  steps.value = steps.value.filter((s) => s.id !== id);
};

const updateStep = (stepId: string, updates: any) => {
  const stepIndex = steps.value.findIndex((s) => s.id === stepId);
  if (stepIndex !== -1) {
    steps.value[stepIndex] = { ...steps.value[stepIndex], ...updates };
  }
};

const saveFlow = async () => {
  const payload = {
    title: flowName.value,
    description: description.value,
    trigger: "MANUAL",
    processes: steps.value.map((step) => ({
      id: step.id,
      title: step.type,
      createdAt: new Date().toISOString().split("T")[0],
    })),
  };

  await FlowDefinitionService.addNewFlowDefinition(payload);
  router.push("/flow-definitions");
};

const goBackToFlowDef = () => router.back();
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
            <label
              class="block text-gray-700 text-sm font-bold mb-2"
              for="flowName"
            >
              Flow Name
            </label>
            <input
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="flowName"
              v-model="flowName"
              type="text"
              placeholder="Enter flow name"
            />
          </div>
        </div>
        <div class="mb-4 w-full">
          <label
            class="block text-gray-700 text-sm font-bold mb-2"
            for="description"
          >
            Description
          </label>
          <textarea
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="description"
            v-model="description"
            rows="4"
            placeholder="Describe what this flow does"
          ></textarea>
        </div>
      </form>

      <p class="font-bold">Flow Steps</p>
      <hr class="pb-5" v-if="steps.length" />

      <!-- Process Cards -->
      <div v-if="steps.length > 0">
        <div
          v-for="(step, index) in steps"
          :key="step.id"
          class="rounded-md border p-5 bg-[#f5f5f5] mb-4"
        >
          <div class="flex justify-between items-center mb-4">
            <p class="font-bold text-gray-700">
              {{ step.type || `Process ${index + 1}` }}
            </p>
            <button
              v-if="steps.length > 0"
              @click="deleteStep(step.id)"
              type="button"
              class="flex items-center gap-1 text-red-500 hover:text-red-700 font-semibold text-sm transition-colors cursor-pointer"
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

          <Process
            :step="step"
            @update-step="(updates) => updateStep(step.id, updates)"
          />
        </div>
      </div>

      <!-- Add Process Button + Dropdown -->
      <div class="relative w-full">
        <button
          @click="showProcessMenu = !showProcessMenu"
          type="button"
          class="w-full rounded-lg border-2 border-dashed border-gray-300 py-4 text-center hover:border-yellow-500 hover:bg-yellow-50 group mt-2 cursor-pointer"
        >
          <span class="font-semibold text-gray-600 group-hover:text-gray-900">
            + Add Process
          </span>
        </button>

        <div
          v-if="showProcessMenu"
          class="absolute z-10 mt-2 w-full bg-white border border-gray-300 rounded-md shadow-lg"
        >
          <button
            @click="addProcess('Notification')"
            class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-yellow-100 transition-colors cursor-pointer"
          >
            Notification Process
          </button>
          <button
            @click="addProcess('Request')"
            class="block w-full text-left px-4 py-2 text-gray-700 hover:bg-yellow-100 transition-colors cursor-pointer"
          >
            Request Process
          </button>
        </div>

        <div v-if="steps.length > 0" class="mt-8 mb-6">
          <p class="font-bold mb-3">Flow Preview</p>
          <FlowDiagram
            :processes="
              steps.map((step) => ({
                id: step.id,
                type: step.type || step.subtype,
                title: step.subtype || step.type,
              }))
            "
            :interactive="true"
          />
        </div>
      </div>

      <hr class="mt-5" />
      <div class="p-5 flex justify-center gap-[2.6rem] mt-2">
        <button
          @click="goBackToFlowDef"
          type="button"
          class="bg-white hover:bg-gray-100 text-gray-700 font-medium py-2 px-6 border border-gray-300 rounded-md shadow-sm transition-colors cursor-pointer"
        >
          Cancel
        </button>
        <button
          type="button"
          class="bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-6 border border-gray-300 rounded-md shadow-sm transition-colors cursor-pointer"
        >
          Save as Draft
        </button>
        <button
          @click="saveFlow"
          type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer"
        >
          Save
        </button>
      </div>
    </div>
  </div>
</template>
