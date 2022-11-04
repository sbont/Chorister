import Vue, { createApp } from 'vue'
import App from './App'
import VueLogger from 'vuejs3-logger';
import router from './router'
import { loadScript } from 'vue-plugin-load-script';
import { createPinia } from 'pinia'
import { useAuth } from "@/stores/authStore";
import { configureCompat } from 'vue'

// default everything to Vue 3 behavior, and only enable compat
// for certain features
configureCompat({
  MODE: 3
})

Vue.config.productionTip = false
// Vue.use(LoadScript);

loadScript("https://kit.fontawesome.com/e168ca8cb0.js")

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
useAuth().init();

const logOptions = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};
app.use(VueLogger, logOptions);
app.use(router);
app.mount('#app')