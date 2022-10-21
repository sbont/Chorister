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
// Vue.use(VueLogger, options);
// Vue.use(auth);
// Vue.use(LoadScript);

loadScript("https://kit.fontawesome.com/e168ca8cb0.js")

/* eslint-disable no-new */
/*new Vue({
  el: '#app',
  template: '<App/>',
  router,
  components: { App },
  pinia,
});*/

const app = createApp(App)
app.use(router);
app.use(auth);
app.config.globalProperties.$auth = authService

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

const pinia = createPinia()
app.use(pinia)
app.mount('#app')