import { createApp } from 'vue'
import App from './App.vue'
import VueLogger from 'vuejs3-logger';
import router from './router'
import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura';
import { loadScript } from 'vue-plugin-load-script';
import { createPinia } from 'pinia'
import { useAuth } from "@/services/authStore";
import { ILoggerOptions } from 'vuejs3-logger/dist/interfaces/logger-options';
import { ApiKey } from './application/api';
import ChoristerApi from './services/api';

loadScript("https://kit.fontawesome.com/e168ca8cb0.js")

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
useAuth().init();

const logOptions = {
    isEnabled: true,
    logLevel: 'debug',
    stringifyArguments: false,
    showLogLevel: true,
    showMethodName: false,
    separator: '|',
    showConsoleColors: true
} as ILoggerOptions;
app.use(VueLogger, logOptions);
app.use(router);
app.use(PrimeVue, {
    theme: {
        preset: Aura
    }
});
app.provide(ApiKey, new ChoristerApi());
app.mount('#app');