import Vue, { createApp } from 'vue'
import App from './App'
import VueLogger from 'vuejs3-logger';
import router from './router'
import auth, {authService} from './auth'
import { loadScript } from 'vue-plugin-load-script';
import { createPinia } from 'pinia'
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
// app.config.globalProperties.$auth = authService
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
app.use(auth);
app.use(pinia)
app.mount('#app')