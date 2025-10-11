<script setup lang="ts">
    const gridStyle = "bg-red-500 h-[10rem] w-[10rem]"
</script>

<template>
  <div class="grid grid-cols-2 w-full max-w-[50vw] gap-4">
    <div :class="gridStyle">
      <p>Hello1</p>
    </div>
    <div :class="gridStyle">
      <p>Hello2</p>
    </div>
    <div :class="gridStyle">
      <p>Hello3</p>
    </div>
    <div :class="gridStyle">
      <p>Hello4</p>
    </div>
  </div>
</template>
