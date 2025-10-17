<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import type { Process } from "../types";

const router = useRouter();

const steps = ref<Process[]>([
  {
    id: 1,
    name: "",
    type: "",
    role: "",
    description: "",
  },
]);
let nextStepId = 2;

const addNextStep = () => {
  steps.value.push({
    id: nextStepId++,
    name: "",
    type: "",
    role: "",
    description: "",
  });
};

const deleteStep = (stepId: number) => {
  if (steps.value.length > 1) {
    steps.value = steps.value.filter((step) => step.id !== stepId);
  }
};

const goBackToFlowDef = () => {
  router.push("/flow-definitions");
};
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <div class="rounded-md border bg-white p-5 border-gray-300 shadow-sm">
      <h1 class="font-extrabold text-4xl pt-5">Create New Flow Definition</h1>

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
        <span>Return to Flow Definitions</span>
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
              type="text"
              placeholder="Enter flow name"
            />
          </div>
          <div>
            <label
              class="block text-gray-700 text-sm font-bold mb-2"
              for="flowType"
            >
              Flow Type
            </label>
            <select
              id="flowType"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
            >
              <option class="text-gray-700" value="">Select flow type</option>
              <option class="text-gray-700" value="type1">Type 1</option>
              <option class="text-gray-700" value="type2">Type 2</option>
              <option class="text-gray-700" value="type3">Type 3</option>
            </select>
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
            rows="4"
            placeholder="Describe what this flow does"
          ></textarea>
        </div>

        <!-- Steps, maybe better to make a component?  -->
      </form>
      <p class="font-bold">Flow Steps</p>
      <hr class="pb-5" />

      <div
        v-for="(step, index) in steps"
        :key="step.id"
        class="rounded-md border p-5 bg-[#f5f5f5] mb-4"
      >
        <div class="flex justify-between items-center mb-4">
          <p class="font-bold text-gray-700">Process {{ index + 1 }}</p>

          <button
            v-if="steps.length > 1"
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

        <label class="block text-sm font-bold mb-2" for="stepName"
          >Process Name</label
        >
        <input
          v-model="step.name"
          class="shadow border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline"
          type="text"
          placeholder="e.g., Manager Approval"
        />

        <div class="mb-4 grid grid-cols-2 gap-4 w-full pt-5">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2"
              >Process Type</label
            >
            <select
              v-model="step.type"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
            >
              <option value="">Select type</option>
              <option value="approval">Approval</option>
              <option value="review">Review</option>
              <option value="notification">Notification</option>
            </select>
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2"
              >Assigned Role</label
            >
            <select
              v-model="step.role"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline cursor-pointer"
            >
              <option value="">Select role</option>
              <option value="manager">Manager</option>
              <option value="admin">Administrator</option>
              <option value="hr">HR</option>
            </select>
          </div>
        </div>

        <div class="mb-4 w-full">
          <label class="block text-gray-700 text-sm font-bold mb-2"
            >Description</label
          >
          <textarea
            v-model="step.description"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline"
            rows="4"
            placeholder="Enter step description"
          ></textarea>
        </div>
      </div>

      <button
        @click="addNextStep"
        type="button"
        class="w-full rounded-lg border-2 border-dashed border-gray-300 py-4 text-center hover:border-yellow-500 hover:bg-yellow-50 group mt-2 cursor-pointer"
      >
        <span class="font-semibold text-gray-600 group-hover:text-gray-900"
          >+ Add Process</span
        >
      </button>

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
          type="button"
          class="bg-yellow-500 hover:bg-yellow-600 text-gray-900 font-semibold py-2 px-6 border border-yellow-600 rounded-md shadow-sm transition-colors cursor-pointer"
        >
          Save
        </button>
      </div>
    </div>
  </div>
</template>
