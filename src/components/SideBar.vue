<script setup lang="ts">
import { ref } from 'vue';

const activeIndex = ref<number>(0);
const menuItems = ['HOME', 'FLOW DEFINITIONS', 'FLOW INSTANCES', 'SETTINGS'];

const yellowBar = 'before:content-[\'\'] before:absolute before:left-0 before:top-0 before:h-full before:w-2 before:bg-sidebarprimary';

const selectOption = (index: number)=>{
    activeIndex.value = index;

    //TODO: route to url
}
</script>

<template>
  <aside class="h-screen bg-sidebarbg py-5 shadow-md flex flex-col items-center">
    <img src="/images/logo.png" alt="Logo" class="w-24 mx-auto block"/>
    <hr class="text-sidebarsecondary mt-4 w-full" />
    
    <nav class="w-full">
      <ul class="mt-5">
        <li
          v-for="(item, index) in menuItems"
          :key="index"
          @click="selectOption(index)"
          class="relative px-[18px] py-[18px] pl-6 font-semibold cursor-pointer transition-colors duration-200 hover:bg-[#f5f5f533]"
          :class="activeIndex === index ? `${yellowBar} text-sidebarprimary bg-sidebarhighlight` : 'text-sidebarsecondary'"
        >
          {{ item }}
        </li>
      </ul>
    </nav>
  </aside>
</template>
