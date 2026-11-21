<script setup lang="ts">
import { computed } from 'vue';
import type { Status } from '../../types';

interface Props {
  status: Status
}

const props = defineProps<Props>()

// The dot colours match the stats cards on the dashboard.
const statusConfig: Record<Status, { dotColor: string; label: string; }> = {
  ACTIVE: {
    dotColor: 'bg-emerald-500',
    label: 'Active',
  },
  PENDING: {
    dotColor: 'bg-amber-500',
    label: 'Pending'
  },
  SUCCESS: {
    dotColor: 'bg-lime-500',
    label: 'Success'
  },
  FAILURE: {
    dotColor: 'bg-red-500',
    label: 'Failed'
  },
  DECLINED: {
    dotColor: 'bg-violet-500',
    label: 'Declined'
  }
}

const config = computed(() => statusConfig[props.status] ?? statusConfig.ACTIVE)

</script>

<template>
  <div class="flex items-center gap-2">
    <span :class="['w-2 h-2 rounded-full', config.dotColor]" aria-hidden="true"></span>
    <span class="font-medium">{{ config.label }}</span>
  </div>
</template>
