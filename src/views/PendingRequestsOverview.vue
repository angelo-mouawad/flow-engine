<script setup lang="ts">
  import PendingRequests from '../components/requests/PendingRequests.vue';
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
  <PendingRequests />
</template>
