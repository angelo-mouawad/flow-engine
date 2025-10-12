<script setup lang="ts">
    const gridStyle = "bg-[#b9b9b9] h-50"
</script>

<template>
  <div class="w-full p-5">
    <p class="font-bold text-[2rem] pb-5">Welcome Manager</p>
    <div class="flex flex-wrap gap-4">
      <div class="w-[49%]">
        <p>Flow Definitions</p>
        <a href="">
          <div :class="gridStyle">
          </div>
        </a>
      </div>
      <div class="w-[49%]">
        <p>Flow Instances</p>
        <a href="">
          <div :class="gridStyle">
          </div>
        </a>
      </div>
      <div class="w-[49%]">
        <p>Diagram</p>
          <a href="">
          <div :class="gridStyle">
          </div>
        </a>
      </div>
      <div class="w-[49%]">
        <p>Something</p>
        <a href="">
          <div :class="gridStyle">
          </div>
        </a>
      </div>
    </div>
  </div>
</template>
