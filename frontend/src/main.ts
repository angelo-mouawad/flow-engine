import { createApp } from 'vue'
import { createPinia } from 'pinia';
import './style.css'
import App from './App.vue'
import router from './router/index.ts';

// Toastification (notifications)
import Toast, { POSITION } from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import { useUserStore } from './stores/userStore.ts';

const app = createApp(App);
const pinia = createPinia();

app.use(Toast, {
    position: POSITION.TOP_RIGHT,
    timeout: 4000,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    draggablePercent: 0.6,
})

app.use(pinia)

app.use(router).mount('#app');

useUserStore().loadFromStorage();