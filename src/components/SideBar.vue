<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeIndex = ref<number>(0);
const isReady = ref<boolean>(false);
const menuItems = ['HOME', 'FLOW DEFINITIONS', 'FLOW INSTANCES', 'SETTINGS'];
const yellowBar = 'before:content-[\'\'] before:absolute before:left-0 before:top-0 before:h-full before:w-2 before:bg-sidebarprimary';
const paths = ['/', '/flow-definitions', '/flow-instances', '/settings'];

const selectOption = (index: number) => {
  activeIndex.value = index;
  router.push(paths[index] as string);
};

onMounted(async () => {
  await router.isReady();
  const currentPath = router.currentRoute.value.path;
  const index = paths.indexOf(currentPath);
  if (index !== -1) {
    activeIndex.value = index;
  }
  isReady.value = true;
});
</script>

<template>
    <aside class="h-screen bg-sidebarbg py-5 shadow-md flex flex-col items-center">
        <img src="/images/logo.png" alt="Logo" class="w-24 mx-auto block"/>
        <hr class="text-sidebarsecondary mt-4 w-full" />

        <nav v-if="isReady" class="w-full">
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
