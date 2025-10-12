<script setup lang="ts">
    const gridElements = ['Flow Definitions', 'Flow Instances', 'Diagram', 'Something'];
</script>

<template>
    <div class="w-full p-5">
        <p class="font-bold text-[2rem] pb-5">Welcome Manager</p>
        <div class="grid grid-cols-2 gap-4">
            <div v-for="(box, index) in gridElements" :key="index" class="flex flex-col">
                <p class="mb-2 font-semibold">{{ box }}</p>
                <RouterLink to="">
                <div class="bg-[#b9b9b9] h-40 rounded hover:opacity-80 transition-opacity"></div>
                </RouterLink>
            </div>
        </div>
    </div>
</template>
