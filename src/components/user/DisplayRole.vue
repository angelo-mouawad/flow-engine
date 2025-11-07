<script setup lang="ts">
  import { defineProps } from 'vue';

  const props = defineProps<{
    role: 'User' | 'Manager';
  }>();
</script>  

<template>    
  <div class="mt-4 text-right">
    <p v-if="role === 'User'" class="text-green-600">
      ✅ Logged in as <strong>User</strong> — Limited Permissions
    </p>
    <p v-else class="text-blue-600">
      ✅ Logged in as <strong>Admin</strong> — Full Permissions
    </p>
  </div>
</template>