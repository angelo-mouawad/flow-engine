<script setup lang="ts">
import { ref } from 'vue';

const activeIndex = ref<number>(0);

const selectedStyle = "bg-[var(--color-sidebarhighlight)] relative before:content-[''] before:absolute before:left-0 before:top-0 before:h-full before:w-2 before:bg-yellow-500"; // optional highlight variable (20% opacity)
const notselected =
  "relative px-[18px] py-[18px] pl-[24px] font-semibold cursor-pointer transition-colors duration-200 hover:bg-[#f5f5f533]";

const setActive = (index: number) => {
  activeIndex.value = index;
};
</script>

<template>
  <aside
    class="h-screen bg-[var(--color-sidebarbg)] py-5 shadow-[2px_0_5px_rgba(0,0,0,0.05)] flex flex-col items-center"
  >
    <img src="/images/logo.png" alt="Logo" class="w-24 mx-auto block" />
    <hr class="text-[var(--color-sidebarsecondary)] mt-4 w-full" />

    <nav class="w-full">
      <ul class="list-none mt-5">
        <li
          @click="setActive(0)"
          :class="activeIndex == 0 ? selectedStyle + ' ' + notselected : notselected"
        >
          <a
            href="#"
            :class="activeIndex == 0 ? 'text-[var(--color-sidebarprimary)]' : 'text-[var(--color-sidebarsecondary)]'"
            class="no-underline font-medium transition-colors duration-200"
          >
            HOME
          </a>
        </li>
        <li
          @click="setActive(1)"
          :class="activeIndex == 1 ? selectedStyle + ' ' + notselected : notselected"
        >
          <a
            href="#"
            :class="activeIndex == 1 ? 'text-[var(--color-sidebarprimary)]' : 'text-[var(--color-sidebarsecondary)]'"
            class="no-underline font-medium transition-colors duration-200"
          >
            FLOW DEFINITIONS
          </a>
        </li>
        <li
          @click="setActive(2)"
          :class="activeIndex == 2 ? selectedStyle + ' ' + notselected : notselected"
        >
          <a
            href="#"
            :class="activeIndex == 2 ? 'text-[var(--color-sidebarprimary)]' : 'text-[var(--color-sidebarsecondary)]'"
            class="no-underline font-medium transition-colors duration-200"
          >
            FLOW INSTANCES
          </a>
        </li>
        <li
          @click="setActive(3)"
          :class="activeIndex == 3 ? selectedStyle + ' ' + notselected : notselected"
        >
          <a
            href="#"
            :class="activeIndex == 3 ? 'text-[var(--color-sidebarprimary)]' : 'text-[var(--color-sidebarsecondary)]'"
            class="no-underline font-medium transition-colors duration-200"
          >
            SETTINGS
          </a>
        </li>
      </ul>
    </nav>
  </aside>
</template>
