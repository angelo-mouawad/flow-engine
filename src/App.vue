<script setup lang="ts">
import SideBar from "./components/SideBar.vue";
import Header from "./components/Header.vue";
import DisplayRole from "./components/user/DisplayRole.vue";
import { ref } from 'vue';

const currentRole = ref<'User' | 'Manager'>('User');
</script>

<template>
  <main class="flex min-h-screen bg-mainbg">
    <SideBar />
    <div class="flex flex-1 flex-col">
      <div class="sticky top-0">
        <Header @role-changed="currentRole = $event" />
      </div>

      <DisplayRole :role="currentRole" class="pr-6"/>

      <div class="flex-1 flex justify-center">
        <div class="w-full p-5 m-5 rounded-xl">
          <router-view />
        </div>
      </div>
    </div>
  </main>
</template>
