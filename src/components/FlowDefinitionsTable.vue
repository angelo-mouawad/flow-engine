<script setup lang="ts">
import type { FlowDefinition } from "../types";
import { ref, onMounted } from "vue";
import FlowDefinitionService from "../services/FlowDefinitionService";

const loading = ref(false);
const flowDefinitions = ref<FlowDefinition[]>([]);
const error = ref<string | null>(null);

const sortKey = ref<'title' | 'updatedAt'>('title');
const sortOrder = ref<'asc' | 'desc'>('asc');

const sortFlowDefinitions = (key: 'title' | 'updatedAt') => {
    if (sortKey.value === key) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortKey.value = key;
        sortOrder.value = 'asc';
    }
    flowDefinitions.value = [...flowDefinitions.value].sort((a, b) => {
        let result = 0;
        if (key === 'title') {
            if (a.title.toLowerCase() < b.title.toLowerCase()) result = -1;
            if (a.title.toLowerCase() > b.title.toLowerCase()) result = 1;
        } else if (key === 'updatedAt') {
            result = new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime();
        }
        return sortOrder.value === 'asc' ? result : -result;
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
        error.value = e instanceof Error ? e.message : 'An error occurred while fetching flow definitions';
        console.error('Failed to fetch flow definitions:', e);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchFlowDefinitions();
});
</script>

<template>
    <div class="w-full">



        <table class="w-full border-collapse text-sm">
            <thead>
                <tr class="text-left text-white bg-[#111]">
                    <th class="px-4 py-2 font-medium select-none">
                        Name
                        <button
                            class="ml-2 inline-flex items-center px-1 py-0.5 border border-gray-300 rounded hover:bg-gray-100 focus:outline-none"
                            @click.stop="sortFlowDefinitions('title')"
                            aria-label="Sort by name"
                        >
                            <span v-if="sortKey === 'title'">
                                <span v-if="sortOrder === 'asc'">▲</span>
                                <span v-else>▼</span>
                            </span>
                            <span v-else class="text-gray-400">⇅</span>
                        </button>
                    </th>
                    <th class="px-4 py-2 font-medium">Description</th>
                    <th class="px-4 py-2 font-medium select-none">
                        Last Updated
                        <button
                            class="ml-2 inline-flex items-center px-1 py-0.5 border border-gray-300 rounded hover:bg-gray-100 focus:outline-none"
                            @click.stop="sortFlowDefinitions('updatedAt')"
                            aria-label="Sort by last updated"
                        >
                            <span v-if="sortKey === 'updatedAt'">
                                <span v-if="sortOrder === 'asc'">▲</span>
                                <span v-else>▼</span>
                            </span>
                            <span v-else class="text-gray-400">⇅</span>
                        </button>
                    </th>
                </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
                <div v-if="loading" class="flex items-center justify-center py-8">
                    <div class="text-gray-600">Loading flow definitions...</div>
                </div>

                <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                    <p class="font-medium">Error</p>
                    <p class="text-sm">{{ error }}</p>
                </div>

                <div v-else-if="flowDefinitions.length === 0" class="text-center py-8 text-gray-500">
                    No flow definitions found.
                </div>
                <tr
                    v-for="def in flowDefinitions"
                    :key="def.id"
                    class="cursor-pointer hover:bg-gray-50 transition-colors"
                >
                    <td class="px-4 py-2 font-medium">{{ def.title }}</td>
                    <td class="px-4 py-2 text-gray-600">{{ def.description || '—' }}</td>
                    <td class="px-4 py-2 text-gray-600">
                        {{ new Date(def.updatedAt).toLocaleDateString('en-GB', { 
                            year: 'numeric', 
                            month: 'long', 
                            day: 'numeric' 
                        }) }}
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>