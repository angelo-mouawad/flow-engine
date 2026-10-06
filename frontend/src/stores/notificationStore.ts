import { Client } from "@stomp/stompjs";
import { ref } from "vue";
import SockJS from "sockjs-client";
import { defineStore } from "pinia";
import { useToast } from "vue-toastification";
import { useUserStore } from "./userStore";
import { apiBaseUrl } from "../services/api";

export type NotificationKind = "success" | "error" | "info";

export interface Notification {
  id: number;
  message: string;
  type: NotificationKind;
  timestamp: Date;
}

const isKind = (value: unknown): value is NotificationKind =>
  value === "success" || value === "error" || value === "info";

export const useNotificationStore = defineStore("notifications", () => {
  const notifications = ref<Notification[]>([]);
  const isConnected = ref(false);
  let stompClient: Client | null = null;
  let nextId = 1;

  const toast = useToast();
  const userStore = useUserStore();

  const addNotification = (message: string, type: NotificationKind = "info") => {
    if (type === "success") toast.success(message);
    else if (type === "error") toast.error(message);
    else toast.info(message);

    notifications.value.unshift({
      id: nextId++,
      message,
      type,
      timestamp: new Date(),
    });
  };

  const removeNotification = (id: number) => {
    notifications.value = notifications.value.filter((n) => n.id !== id);
  };

  const connectWebSocket = () => {
    const userEmail = userStore.currentUser?.email;
    if (!userEmail || stompClient?.active) return;

    stompClient = new Client({
      webSocketFactory: () => new SockJS(`${apiBaseUrl}/ws`),
      reconnectDelay: 5000,
      debug: (msg) => {
        if (import.meta.env.DEV) console.debug(msg);
      },
      onConnect: () => {
        isConnected.value = true;
        stompClient?.subscribe(`/topic/notification/${userEmail}`, (payload) => {
          try {
            const data = JSON.parse(payload.body);
            if (typeof data?.message === "string") {
              addNotification(data.message, isKind(data.type) ? data.type : "info");
            }
          } catch {
            console.warn("Ignoring a notification that was not valid JSON");
          }
        });
      },
      onDisconnect: () => {
        isConnected.value = false;
      },
      onWebSocketClose: () => {
        isConnected.value = false;
      },
      onStompError: (frame) => {
        console.warn("Notification channel error:", frame.headers.message);
      },
    });

    stompClient.activate();
  };

  const disconnectWebSocket = () => {
    if (stompClient) {
      void stompClient.deactivate();
      stompClient = null;
    }
    isConnected.value = false;
  };

  return {
    notifications,
    addNotification,
    removeNotification,
    isConnected,
    connectWebSocket,
    disconnectWebSocket,
  };
});
