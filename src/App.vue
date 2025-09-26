<script setup lang="ts">
  import { onMounted, ref } from 'vue';

  const date = ref<String>('');

  const updateDate = ()=>{
    const now = new Date();
    const hours = now.getHours().toString();
    const minutes = now.getMinutes().toString();
    const seconds = now.getSeconds().toString();

    date.value = `${hours}:${minutes}:${seconds}`;
  };

  onMounted(()=>{
    updateDate();
    setInterval(updateDate, 1000);
  });
</script>

<template>
  <main class="w-full h-[100vh] flex flex-wrap items-center justify-center bg-mainbg">
    <div class="flex justify-center flex-wrap text-center">
      <h1 class="w-full text-7xl text-maintxt font-extrabold">Dynamic Flow Engine</h1>
      <span class="text-3xl text-maintxt font-medium">{{date}}</span>
    </div>
  </main>
</template>
