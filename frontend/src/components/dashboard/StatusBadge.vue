<script setup lang="ts">
import { computed } from 'vue';
import type { Status } from '../../types';

interface Props {
  status: Status
}

const props = defineProps<Props>()

const statusConfig: Record<Status, { textColor: string; bgColor: string; label: string; }> = {
  ACTIVE: {
    textColor: 'text-status-active',
    bgColor: 'bg-statuc-active',
    label: 'Active',
  },
  PENDING: {
    textColor: 'text-status-pending',
    bgColor: 'bg-status-pending',
    label: 'Pending'
  },
  SUCCESS: {
    textColor: 'text-status-success',
    bgColor: 'bg-status-success',
    label: 'Success'
  },
  FAILURE: {
    textColor: 'text-status-failure',
    bgColor: 'bg-status-failure',
    label: 'Failed'
  }
}

const config = computed(() => statusConfig[props.status] ?? statusConfig.ACTIVE)

</script>

<template>
  <div class="flex items-center gap-2">
    <span 
      :class="['w-2 h-2 rounded-full', config.textColor]"></span>
    <span :class="['font-medium', config.bgColor]">{{ config.label }}</span>
  </div>
</template>