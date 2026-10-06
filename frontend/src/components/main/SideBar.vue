<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";

const user = ref<any>(null)
const stored = sessionStorage.getItem("user")

if (stored) {
  user.value = JSON.parse(stored)
}

const isManager = () => {
  return user.value?.role === "Manager";
}

const router = useRouter();
const route = useRoute();
const activeIndex = computed(() => {
  const normalised = `/${route.path.split("/")[1]}`;
  return paths.indexOf(normalised);
});
const isReady = ref<boolean>(false);
const managerMenuItems = ["HOME", "FLOW DEFINITIONS", "FLOW INSTANCES", "MANAGE REQUESTS", "SETTINGS"];
const userMenuItems = ["HOME", "FLOW INSTANCES", "SETTINGS"];
const yellowBar =
  "before:content-[''] before:absolute before:left-0 before:top-0 before:h-full before:w-2 before:bg-sidebarprimary";
const managerPaths = ["/", "/flow-definitions", "/flow-instances", "/pending-requests", "/settings"];
const userPaths = ["/", "/flow-instances", "/settings"];

const menuItems = isManager() ? managerMenuItems : userMenuItems;
const paths = isManager() ? managerPaths : userPaths;

const burgerOpen = ref(false);

const selectOption = (index: number) => {
  router.push(paths[index] as string);
  burgerOpen.value = false;
};

onMounted(async () => {
  await router.isReady();
  isReady.value = true;
});
</script>

<template>
  <div class="md:hidden flex items-center h-20 bg-sidebarbg px-5 shadow-md fixed top-0 left-0 right-0 z-40">
    <button @click="burgerOpen = !burgerOpen" class="focus:outline-none">
      <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
      </svg>
    </button>
    <div class="flex-1 flex justify-center">
      <img src="/images/logo.png" alt="Logo" class="h-12" />
    </div>
  </div>

  <div class="md:hidden h-20"></div>

  <transition name="fade">
    <div v-if="burgerOpen" class="fixed inset-0 bg-black/50 z-40 md:hidden" @click.self="burgerOpen = false">
      <aside class="absolute left-0 top-0 h-full w-64 bg-sidebarbg py-6 px-4 shadow-xl flex flex-col z-50"
        aria-label="Sidebar navigation">
        <div class="flex items-center mb-6">
          <img src="/images/logo.png" alt="Logo" class="w-10 mr-2" />
          <button @click="burgerOpen = false" class="ml-auto text-2xl text-gray-600 hover:text-black">&times;</button>
        </div>
        <nav v-if="isReady" aria-label="Sidebar navigation">
          <ul>
            <li v-for="(item, index) in menuItems" :key="index" @click="selectOption(index)"
              class="relative px-4 py-3 font-semibold cursor-pointer transition-colors duration-200 hover:bg-[#f5f5f533] rounded"
              :class="activeIndex === index ? 'text-sidebarprimary bg-sidebarhighlight' : 'text-sidebarsecondary'">
              {{ item }}
            </li>
          </ul>
        </nav>
      </aside>
    </div>
  </transition>

  <aside
    class="hidden md:sticky md:flex md:h-screen md:w-52 lg:w-60 xl:w-68 top-0 left-0 bg-sidebarbg py-5 shadow-md flex-col items-center z-30"
    aria-label="Sidebar navigation">
    <img src="/images/logo.png" alt="Logo" class="w-12 h-12 mx-auto block" />
    <hr class="border-t border-sidebarsecondary/30 mt-4 w-10/12" />
    <nav v-if="isReady" class="w-full overflow-x-auto md:overflow-visible" aria-label="Sidebar navigation">
      <ul class="mt-5">
        <li v-for="(item, index) in menuItems" :key="index" @click="selectOption(index)"
          class="relative px-[18px] py-[10px] pl-6 font-semibold cursor-pointer transition-colors duration-200 hover:bg-[#f5f5f533]"
          :class="activeIndex === index ? `${yellowBar} text-sidebarprimary bg-sidebarhighlight` : 'text-sidebarsecondary'">
          {{ item }}
        </li>
      </ul>
    </nav>
  </aside>
</template>