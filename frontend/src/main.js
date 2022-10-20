import Vue from 'vue'
import App from './App'
import VueLogger from 'vuejs-logger';
import router from './router'
import auth from './auth'
import LoadScript from 'vue-plugin-load-script';
import { createPinia, PiniaVuePlugin } from 'pinia'

Vue.config.productionTip = false

Vue.use(VueLogger, options);
Vue.use(auth);
Vue.use(LoadScript);
Vue.use(PiniaVuePlugin)

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

const pinia = createPinia()
Vue.loadScript("https://kit.fontawesome.com/e168ca8cb0.js")

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  router,
  components: { App },
  pinia,
});

export const eventBus = new Vue();