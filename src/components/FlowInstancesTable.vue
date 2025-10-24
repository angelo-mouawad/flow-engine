<script setup lang="ts">
    import type { FlowInstance} from "../types";
    import { ref } from "vue";

    const instances = ref<FlowInstance[]>([]);

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
