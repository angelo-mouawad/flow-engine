<script setup lang="ts">
  import SideBar from './components/SideBar.vue';
  import WelcomeGrid from './components/WelcomeGrid.vue';
</script>

<template>
  <main class="flex">
    <div>
      <SideBar />
    </div>
    <div class="w-full flex items-center justify-center">
      <WelcomeGrid /> 
    </div>
  </main>
</template>