<script setup lang="ts">
    import type { FlowInstance} from "../types";
    import { ref, onMounted } from "vue";
    import FlowInstanceService from "../services/FlowInstanceService";

    const loading = ref(false);
    const flowInstances = ref<FlowInstance[]>([]);
    const error = ref<string | null>(null);

    const fetchFlowInstances = async () => {
        loading.value = true;
        error.value = null;
        try {
            flowInstances.value = await FlowInstanceService.getFlowInstances();
        } catch (e) {
            error.value = e instanceof Error ? e.message : 'An error occurred while fetching flow instances';
            console.error('Failed to fetch flow instances:', e);
        } finally {
            loading.value = false;
        }
    }

    onMounted(() => {
        fetchFlowInstances();
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
            <div v-if="loading" class="flex items-center justify-center py-8">
                <div class="text-gray-600">Loading flow instances...</div>
            </div>

            <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                <p class="font-medium">Error</p>
                <p class="text-sm">{{ error }}</p>
            </div>

            <div v-else-if="flowInstances.length === 0" class="text-center py-8 text-gray-500">
                No flow instances found.
            </div>
            <tr
                class="cursor-pointer hover:bg-gray-100"
                v-for="inst in flowInstances"
                :key="inst.id"
            >
                <td class="px-4 py-2">{{ inst.title }}</td>
                <td class="px-4 py-2">{{ inst.status }}</td>
                <td class="px-4 py-2">{{ inst.flowDefinition.title}}</td>
            </tr>
        </tbody>
    </table>
</template>
