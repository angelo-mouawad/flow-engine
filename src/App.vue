<script setup lang="ts">
  import SideBar from './components/SideBar.vue';

</script>

<template>
  <main class="flex">
    <div>
      <SideBar />
    </div>
    <div class="w-full flex items-center justify-center">
      <div class="w-full p-5 m-5 border rounded-xl flex">
        <router-view/>
      </div>
    </div>
  </main>
</template>
