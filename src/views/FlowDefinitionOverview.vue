<script setup lang="ts">
  import FlowTypeCreation from '../components/flows/FlowTypeCreation.vue';
  import { ref } from 'vue';
  import router from '../router';

  const user = ref<any>(null)
  const stored = sessionStorage.getItem("user")

  if (stored) {
    user.value = JSON.parse(stored)
  }

  if (user.value?.role == "User") {
    router.push({ name: "Home" });
  }
</script>

<template>
  <FlowTypeCreation :is-definition="true"/>
</template>
