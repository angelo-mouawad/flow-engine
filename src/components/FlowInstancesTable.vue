<script setup lang="ts">
    import type { FlowInstance} from "../types";
    import { onMounted, ref} from "vue";
    import definitions from "./FlowDefinitionsTable.vue";

    const instances = ref<FlowInstance[]>([]);

    const loadData = () => {
        instances.value = [
        {
            id: '1',
            flowDefinition: definitions.value[0]!,
            title: 'Onboarding — John Doe',
            status: "ACTIVE",
            currentProcess: { id: '2', name: 'Assign Equipment', type: 'test', role: 'test', description: 'test' },
            updatedAt: new Date('2025-10-10T09:30:00'),
        },
        {
            id: '2',
            flowDefinition: definitions.value[1]!,
            title: 'Purchase Request #2415',
            status: "PENDING",
            currentProcess: { id: 'p4', name: 'Submit Request', type: 'test', role: 'test', description: 'test' },
            updatedAt: new Date('2025-10-11T14:45:00'),
        },
        {
            id: '3',
            flowDefinition: definitions.value[2]!,
            title: 'Feedback — ACME Corp',
            status: 'SUCCESS',
            currentProcess: { id: 'p9', name: 'Close Feedback', type: 'test', role: 'test', description: 'test' },
            updatedAt: new Date('2025-10-12T11:00:00'),
        },];
    };

    onMounted(() => {
        loadData();
        });
</script>

<template>
    <table class="w-full border-collapse text-sm">
        <thead>
            <tr class="text-left text-white bg-[#111]">
                <th class="px-4 py-2 font-medium">Name</th>
                <th class="px-4 py-2 font-medium">Status</th>
                <th class="px-4 py-2 font-medium">Flow Definition</th>
            </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
            <tr
                class="cursor-pointer hover:bg-gray-100"
                v-for="inst in instances"
                :key="inst.id"
            >
                <td class="px-4 py-2">{{ inst.title }}</td>
                <td class="px-4 py-2">{{ inst.status }}</td>
                <td class="px-4 py-2">{{ inst.flowDefinition}}</td>
            </tr>
        </tbody>
    </table>
</template>