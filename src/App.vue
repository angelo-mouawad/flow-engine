<script setup lang="ts">
import SideBar from "./components/main/SideBar.vue";
import Header from "./components/main/Header.vue";
import { computed } from 'vue';
import { useRoleStore } from "./stores/roleStore";

const roleStore = useRoleStore();
const currentRole = computed({
  get: () => roleStore.role,
  set: (v) => roleStore.setRole(v)
})
</script>

<template>
  <main class="min-h-screen bg-mainbg flex flex-col md:flex-row">
    <SideBar />
    <div class="flex-1 flex flex-col min-h-screen w-full overflow-x-hidden">
      <div class="sticky top-0 z-20">
        <Header @role-changed="currentRole = $event" />
      </div>

      <div class="flex-1">
        <div class="w-full max-w-7xl mx-auto p-4 sm:p-6">
          <router-view />
        </div>
      </div>
    </div>
  </main>
</template>