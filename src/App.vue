<script setup lang="ts">
import SideBar from "./components/SideBar.vue";
import Header from "./components/Header.vue";
</script>

<template>
  <main class="flex min-h-screen bg-color-mainbg">
    <SideBar />
    <div class="flex flex-1 flex-col">
      <div class="sticky top-0">
        <Header />
      </div>
      <div class="flex-1 flex justify-center">
        <div class="w-full p-5 m-5 rounded-xl">
          <router-view />
        </div>
      </div>
    </div>
  </main>
</template>
