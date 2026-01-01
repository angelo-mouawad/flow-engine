import { Client } from '@stomp/stompjs';
import { ref } from 'vue';
import SockJS from 'sockjs-client';
import { defineStore } from 'pinia';
import { useUserStore } from './userStore';
import { useToast } from 'vue-toastification';

export interface Notification {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
  timestamp: Date
}

export const useNotificationStore = defineStore('notifications', () => {
  const notifications = ref<Notification[]>([]);
  const isConnected = ref(false);
  let stompClient: Client | null = null;

  const toast = useToast();
  const userStore = useUserStore();

  const addNotification = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
    if (type === 'success') toast.success(message);
    else if (type === 'error') toast.error(message);
    else toast.info(message);
    
    notifications.value.unshift({
      id: Date.now(),
      message,
      type,
      timestamp: new Date(),
    });
  };

  const removeNotification = (id: number) => {
    notifications.value = notifications.value.filter((n) => n.id !== id)
  };

  const connectWebSocket = () => {
    const userEmail = userStore.currentUser?.email;

    if (!userEmail) {
      console.log('[STOMP]: Connection postponed - No user selected.');
      return;
    }

    if (stompClient?.active) return;

    const socket = new SockJS('http://localhost:8080/ws');

    stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (msg) => { if (import.meta.env.DEV) console.log(msg)},
      onConnect: () => {
        isConnected.value = true;
        const topic = `/topic/notification/${userEmail}`;
        stompClient?.subscribe(topic, (payload) => {
          const data = JSON.parse(payload.body);
          addNotification(data.message, data.type || 'info');
        });
      },
      onDisconnect: () => {
        isConnected.value = false;
      },
    });

    stompClient.activate();
  };

  const disconnectWebSocket = () => {
    if (stompClient) {
      stompClient.deactivate();
      isConnected.value = false;
    }
  }

  return { notifications, addNotification, removeNotification, isConnected, connectWebSocket, disconnectWebSocket, clearInterval }
});
