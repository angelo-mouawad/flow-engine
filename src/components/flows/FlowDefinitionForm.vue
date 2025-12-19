<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import FlowDefinitionService from "../../services/FlowDefinitionService";
import FlowDiagram from "./FlowDiagram.vue";
import vSelect from "vue-select";
import "vue-select/dist/vue-select.css";
import type {
  NotificationType,
  Process,
  ProcessType,
  RequestType,
} from "../../types";
import { useThemeStore } from "../../stores/themeStore";
import { useUserStore } from "../../stores/userStore";

const router = useRouter();

const flowName = ref("");
const flowNameError = ref("");
const descriptionError = ref("");
const stepsError = ref("");
const description = ref("");
const steps = ref<Process[]>([]);
const showProcessMenu = ref(false);
const triggerableBy = ref<string[]>([]);

const addProcess = (processType: ProcessType) => {
  let newProcess: Process;

  if (processType === "REQUEST") {
    newProcess = {
      processType: "REQUEST",
      name: "",
      requestTypeName: "" as RequestType,
      approvable: false,
      minApprovals: 1,
      approvableBy: [],
      step: steps.value.length,
    };
  } else if (processType === "APPROVAL") {
    newProcess = {
      processType: "APPROVAL",
      requestSteps: [],
      name: "",
      step: steps.value.length + 1,
    };
  } else {
    newProcess = {
      processType: "NOTIFICATION",
      name: "",
      notificationType: "" as NotificationType,
      toNotify: [],
      requestStep: undefined,
      step: steps.value.length + 1,
    };
  }
  steps.value.push(newProcess);
  renumberSteps();
  showProcessMenu.value = false;
};

const deleteStep = (index: number) => {
  steps.value.splice(index, 1);

  steps.value.forEach((step) => {
    if (step.processType === "APPROVAL" && Array.isArray(step.requestSteps)) {
      step.requestSteps = step.requestSteps
        .map((reqIndex) => {
          if (reqIndex === index) return null; 
          if (reqIndex > index) return reqIndex - 1; 
          return reqIndex;
        })
        .filter((reqIndex): reqIndex is number => reqIndex !== null);
    }

    if (step.processType === "NOTIFICATION" && typeof step.requestStep === "number") {
      if (step.requestStep === index) {
        step.requestStep = undefined; 
      } else if (step.requestStep > index) {
        step.requestStep = step.requestStep - 1; 
      }
    }
  });
  
  renumberSteps();
};

const renumberSteps = () => {
  steps.value.forEach((s, i) => {
    s.step = i;
  });
};

const validateFlowDefinition = (): boolean => {
  flowNameError.value = "";
  descriptionError.value = "";
  stepsError.value = "";

  if (!flowName.value.trim()) {
    flowNameError.value = "Flow name is required";
    return false;
  }

  if (!description.value.trim()) {
    descriptionError.value = "Description is required";
    return false;
  }

  if (steps.value.length === 0) {
    stepsError.value = "At least one process is required";
    return false;
  }

  const requestsWithoutType = steps.value.filter(
    (step) => step.processType === "REQUEST" && !step.requestTypeName
  );
  if (requestsWithoutType.length > 0) {
    stepsError.value =
      "All REQUEST processes must have a request type selected";
    return false;
  }

  const notificationsWithoutType = steps.value.filter(
    (step) => step.processType === "NOTIFICATION" && !step.notificationType
  );
  if (notificationsWithoutType.length > 0) {
    stepsError.value =
      "All NOTIFICATION processes must have a notification type selected";
    return false;
  }

  const approvableWithoutApprovers = steps.value.filter(
    (step) =>
      step.processType === "REQUEST" &&
      step.approvable &&
      (!step.approvableBy || step.approvableBy.length === 0)
  );
  if (approvableWithoutApprovers.length > 0) {
    stepsError.value = "Approvable requests must have at least one approver";
    return false;
  }

  const approvableWithInvalidMin = steps.value.filter(
    (step) =>
      step.processType === "REQUEST" &&
      step.approvable &&
      (!step.minApprovals || step.minApprovals < 1)
  );
  if (approvableWithInvalidMin.length > 0) {
    stepsError.value = "Approvable requests must have minimum approvals >= 1";
    return false;
  }

  const approvableWithTooHighMin = steps.value.filter(
    (step) =>
      step.processType === "REQUEST" &&
      step.approvable &&
      step.minApprovals &&
      step.approvableBy &&
      step.minApprovals > step.approvableBy.length
  );
  if (approvableWithTooHighMin.length > 0) {
    stepsError.value = "Minimum approvals cannot exceed number of approvers";
    return false;
  }

  const hasApproval = steps.value.some(
    (step) => step.processType === "APPROVAL"
  );
  const hasApprovableRequest = steps.value.some(
    (step) => step.processType === "REQUEST" && step.approvable
  );

  if (hasApproval && !hasApprovableRequest) {
    stepsError.value =
      "APPROVAL process requires at least one approvable REQUEST";
    return false;
  }

  if (hasApprovableRequest && !hasApproval) {
    stepsError.value = "Flow has approvable requests but no APPROVAL process";
    return false;
  }

  return true;
};

const saveFlow = async () => {
  if (!validateFlowDefinition()) {
    return;
  }

  const payload = {
    title: flowName.value,
    description: description.value,
    triggerableBy: triggerableBy.value,
    processes: steps.value.map((step) => {
      const { id, ...processData } = step;

      if (!processData.name || processData.name.trim() === "") {
        if (processData.processType === "REQUEST") {
          const requestTypeName = processData.requestTypeName;
          if (requestTypeName === "ABSENCE_REQUEST") {
            processData.name = "Absence Request";
          } else if (requestTypeName === "CLOCKIN_REQUEST") {
            processData.name = "Clock-In Request";
          } else {
            processData.name = "Request Process";
          }
        } else if (processData.processType === "APPROVAL") {
          processData.name = "Approval Process";
        } else if (processData.processType === "NOTIFICATION") {
          const notificationType = processData.notificationType;
          if (notificationType === "EMAIL_NOTIFICATION") {
            processData.name = "Email Notification";
          } else if (notificationType === "POPUP_NOTIFICATION") {
            processData.name = "Popup Notification";
          } else {
            processData.name = "Notification Process";
          }
        }
      }

      return processData;
    }),
  };

  await FlowDefinitionService.addNewFlowDefinition(payload);
  router.push("/flow-definitions");
};

const userStore = useUserStore();
const availableTriggers = computed(() => userStore.users);
const availableApprovers = computed(() =>
  userStore.users.filter((user) => user.role === "Manager")
);

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
      isDarkMode ? 'bg-[#1c1e1f] text-gray-100' : 'bg-gray-100 text-gray-900',
    ]"
  >
    <div class="w-full max-w-4xl mx-auto px-4">
      <div
        class="rounded-md border p-5 shadow-sm transition-colors duration-300"
        :class="
          isDarkMode
            ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
            : 'bg-white border-gray-300 text-gray-900'
        "
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
            class="flex items-center w-fit mb-2 transition-colors font-medium mt-2 cursor-pointer text-sidebarprimary hover:text-yellow-600"
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
                for="flowName"
                class="block text-sm font-bold mb-2"
                :class="labelTextColor"
              >
                Flow Name
              </label>
              <input
                id="flowName"
                v-model="flowName"
                type="text"
                placeholder="Enter flow name"
                :class="[
                  'shadow border rounded w-full py-2 px-3 transition-colors duration-200 placeholder-gray-400',
                  isDarkMode
                    ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                    : 'bg-white border-gray-300 text-gray-700',
                  flowNameError ? 'border-red-500 focus:border-red-500' : '',
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
              for="flowDescription"
            >
              Description
            </label>
            <textarea
              id="flowDescription"
              v-model="description"
              rows="4"
              placeholder="Describe what this flow does"
              :class="[
                'shadow border rounded w-full py-2 px-3 transition-colors duration-200 placeholder-gray-400',
                isDarkMode
                  ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                  : 'bg-white border-gray-300 text-gray-700',
                descriptionError ? 'border-red-500 focus:border-red-500' : '',
              ]"
            ></textarea>
            <p v-if="descriptionError" class="text-red-600 text-sm mt-1">
              {{ descriptionError }}
            </p>
          </div>

          <div class="mb-6 w-full">
            <label
              for="flowTriggers"
              class="block text-sm font-bold mb-2"
              :class="labelTextColor"
            >
              Triggerable By
            </label>

            <v-select
              v-model="triggerableBy"
              :options="availableTriggers"
              label="name"
              :reduce="(user: any) => user.email"
              placeholder="Search and select users..."
              multiple
            >
              <template #option="{ name, email, role }">
                <div class="flex justify-between items-center">
                  <span>{{ name }} ({{ email }})</span>
                  <span class="text-xs opacity-75">{{ role }}</span>
                </div>
              </template>
            </v-select>

            <p class="text-xs text-gray-500 mt-2">
              If no users are selected, anyone can trigger this flow
            </p>
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
        <p v-if="stepsError" class="text-red-600 text-sm mt-1 mb-2">
          {{ stepsError }}
        </p>

        <div v-if="steps.length > 0">
          <div
            v-for="(step, index) in steps"
            :key="index"
            class="rounded-md border p-5 mb-4 transition-colors duration-200"
            :class="
              isDarkMode
                ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                : 'bg-[#f5f5f5] border-gray-300 text-gray-800'
            "
          >
            <div class="flex justify-between items-center mb-4">
              <p class="font-bold">{{ step.processType }} PROCESS</p>
              <button
                @click="deleteStep(index)"
                type="button"
                class="flex items-center gap-1 font-semibold text-sm transition-colors cursor-pointer"
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

            <div class="mb-4">
              <label
                class="block text-sm font-bold mb-2"
                :class="labelTextColor"
                for="flowProcess"
              >
                Process Name
              </label>
              <input
                id="flowProcess"
                v-model="step.name"
                type="text"
                placeholder="Enter process name"
                :class="[
                  'shadow border rounded w-full py-2 px-3 transition-colors duration-200 placeholder-gray-400',
                  isDarkMode
                    ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                    : 'bg-white border-gray-300 text-gray-700',
                ]"
              />
            </div>

            <template v-if="step.processType === 'REQUEST'">
              <div class="mb-4">
                <label
                  for="flowRequestType"
                  class="block text-sm font-bold mb-2"
                  :class="labelTextColor"
                >
                  Request Type
                </label>
                <v-select
                  v-model="step.requestTypeName"
                  :options="[
                    { label: 'Absence Request', value: 'ABSENCE_REQUEST' },
                    { label: 'Clock-In Request', value: 'CLOCKIN_REQUEST' },
                  ]"
                  :reduce="(option: any) => option.value"
                  label="label"
                  placeholder="Select request type..."
                  :clearable="false"
                />
              </div>

              <div class="mb-4 flex items-center">
                <input
                  v-model="step.approvable"
                  type="checkbox"
                  :id="`approvable-${index}`"
                  class="mr-2 h-5 w-5 cursor-pointer border rounded-md appearance-none transition-colors checked:bg-sidebarprimary"
                  :class="
                    isDarkMode
                      ? 'bg-[#181a1b] border-[#2c2f31]'
                      : 'bg-gray-50 bg-gray-200'
                  "
                />
                <label
                  :for="`approvable-${index}`"
                  class="text-sm font-bold cursor-pointer"
                  :class="labelTextColor"
                >
                  Requires Approval
                </label>
              </div>

              <template v-if="step.approvable">
                <div class="mb-4">
                  <label
                    for="flowMinApprovalsChecker"
                    class="block text-sm font-bold mb-2"
                    :class="labelTextColor"
                  >
                    Minimum Approvals Required
                  </label>
                  <input
                    id="flowMinApprovalsChecker"
                    v-model.number="step.minApprovals"
                    type="number"
                    min="1"
                    placeholder="Enter minimum approvals"
                    :class="[
                      'shadow border rounded w-full py-2 px-3 transition-colors duration-200',
                      isDarkMode
                        ? 'bg-[#1c1e1f] border-[#2c2f31] text-white'
                        : 'bg-white border-gray-300 text-gray-700',
                    ]"
                  />
                </div>

                <div class="mb-4">
                  <label
                    for="flowApprovers"
                    class="block text-sm font-bold mb-2"
                    :class="labelTextColor"
                  >
                    Approvers
                  </label>

                  <v-select
                    v-model="step.approvableBy"
                    :options="availableApprovers"
                    label="name"
                    :reduce="(user: any) => user.email"
                    placeholder="Search and select approvers..."
                    multiple
                  >
                    <template #option="{ name, email, role }">
                      <div class="flex justify-between items-center">
                        <span>{{ name }} ({{ email }})</span>
                        <span class="text-xs opacity-75">{{ role }}</span>
                      </div>
                    </template>
                  </v-select>
                </div>
              </template>
            </template>

            <template v-if="step.processType === 'APPROVAL'">
              <div class="mb-4">
                <label
                  for="flowApprovalTargets"
                  class="block text-sm font-bold mb-2"
                  :class="labelTextColor"
                >
                  Select Request Process(es) to Approve
                </label>

                <v-select
                  v-model="step.requestSteps"
                  :options="steps
                    .filter((p, i) => p.processType === 'REQUEST' && i < index)
                    .map(p => ({ label: p.name || 'Request Process', value: p.step }))"
                  label="label"
                  :reduce="(option: any) => option.value"
                  placeholder="Select request processes to approve..."
                  multiple
                />
                <p class="text-xs text-gray-500 mt-2">
                  Choose one or more request processes that this approval will handle.
                </p>
              </div>
            </template>

            <template v-if="step.processType === 'NOTIFICATION'">
              <div class="mb-4">
                <label
                  for="flowNotificationType"
                  class="block text-sm font-bold mb-2"
                  :class="labelTextColor"
                >
                  Notification Type
                </label>
                <v-select
                  v-model="step.notificationType"
                  :options="[
                    {
                      label: 'Email Notification',
                      value: 'EMAIL_NOTIFICATION',
                    },
                    {
                      label: 'Popup Notification',
                      value: 'POPUP_NOTIFICATION',
                    },
                  ]"
                  :reduce="(option: any) => option.value"
                  label="label"
                  placeholder="Select notification type..."
                  :clearable="false"
                />
              </div>

              <div class="mb-4">
                <label
                  for="flowNotificationUsers"
                  class="block text-sm font-bold mb-2"
                  :class="labelTextColor"
                >
                  Select User(s) to Notify
                </label>

                <v-select
                  v-model="step.toNotify"
                  :options="availableTriggers"
                  label="name"
                  :reduce="(user: any) => user.email"
                  placeholder="Search and select users to notify..."
                  multiple
                >
                  <template #option="{ name, email, role }">
                    <div class="flex justify-between items-center">
                      <span>{{ name }} ({{ email }})</span>
                      <span class="text-xs opacity-75">{{ role }}</span>
                    </div>
                  </template>
                </v-select>
                <p class="text-xs text-gray-500 mt-2">
                  Leave empty to use default notification recipients.
                </p>
              </div>

              <div class="mb-4">
                <label
                  for="flowNotificationTargets"
                  class="block text-sm font-bold mb-2"
                  :class="labelTextColor"
                >
                  Select Request Process to Approve
                </label>

                <v-select
                  v-model="step.requestStep"
                  :options="steps
                    .filter((p, i) => p.processType === 'REQUEST' && i < index)
                    .map(p => ({ label: p.name || 'Request Process', value: p.step }))"
                  label="label"
                  :reduce="(option: any) => option.value"
                  placeholder="Select a request process to approve..."
                />
                <p class="text-xs text-gray-500 mt-2">
                  Choose the request process this notification will relate to.
                </p>
              </div>
            </template>
          </div>
        </div>

        <div class="relative w-full">
          <button
            @click="showProcessMenu = !showProcessMenu"
            type="button"
            class="w-full rounded-lg border-2 border-dashed py-4 text-center mt-2 transition-colors cursor-pointer"
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
              v-for="type in ['REQUEST', 'APPROVAL', 'NOTIFICATION']"
              :key="type"
              @click="addProcess(type as ProcessType)"
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
            <FlowDiagram :processes="steps" :interactive="true" />
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
