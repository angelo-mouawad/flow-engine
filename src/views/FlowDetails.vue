<script setup lang="ts">
    import { onMounted, ref } from 'vue';
    import type { FlowDefinition } from '../types';

    defineProps(['isDefinition'])

    const definitions = ref<FlowDefinition[]>([]);

    onMounted(()=>{
    });
</script>

<template>
    <div class="w-[50%] rounded-md border p-5">
        <h1 class="font-extrabold text-4xl py-5">{{ isDefinition?'Flow Definitions: ':'Flow Instances: ' }}</h1>
        <div class="flex items-center justify-between mb-2">
            <input 
            type="text" 
            placeholder="Search" 
            class="border rounded-md px-3 py-1 text-sm focus:outline-none"
            />
            <RouterLink to="/flow-definitions-new" >
                <button class="rounded-md px-4 py-1 bg-yellow-500 text-sm font-medium border cursor-pointer hover:bg-yellow-600">+ New flow</button>
            </RouterLink>
        </div>

        <div class="overflow-x-auto rounded-md">
            <table class="w-full border-collapse text-sm rounded-md">
                <thead>
                    <tr class="text-left text-white bg-[#111]">
                        <th class="px-4 py-2 font-medium">Name</th>
                        <th class="px-4 py-2 font-medium">Process</th>
                        <th class="px-4 py-2 font-medium">Created At</th>
                    </tr>
                </thead>
                <tbody>
                    <tr 
                        class="cursor-pointer hover:bg-gray-100"
                        v-for="def in definitions" >
                        <td class="px-4 py-2">{{def.title}}</td>
                        <td class="px-4 py-2">{{def.description}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>
