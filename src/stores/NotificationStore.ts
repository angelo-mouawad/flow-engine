import { ref } from 'vue'

export interface Notification {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
  timestamp: Date
}

const notifications = ref<Notification[]>([])

export function useNotificationStore() {
  const addNotification = (message: string, type: 'success' | 'error' | 'info') => {
    notifications.value.push({
      id: Date.now(),
      message,
      type,
      timestamp: new Date(),
    })
  }

  const removeNotification = (id: number) => {
    notifications.value = notifications.value.filter((n) => n.id !== id)
  }

  return { notifications, addNotification, removeNotification }
}