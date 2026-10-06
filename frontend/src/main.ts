import { createApp } from "vue";
import { createPinia } from "pinia";
import Toast, { POSITION } from "vue-toastification";
import "vue-toastification/dist/index.css";
import "./style.css";
import App from "./App.vue";
import router from "./router";
import { useUserStore } from "./stores/userStore";

const app = createApp(App);

app.use(createPinia());
useUserStore().loadFromStorage();

app.use(Toast, {
  position: POSITION.TOP_RIGHT,
  timeout: 4000,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.6,
});

app.config.errorHandler = (error) => {
  console.error("Unexpected error:", error);
};

app.use(router).mount("#app");
