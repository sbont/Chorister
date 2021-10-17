import Vue from 'vue'
import App from './App'
import VueLogger from 'vuejs-logger';
import router from './router'
import auth from './auth'
import LoadScript from 'vue-plugin-load-script';

Vue.config.productionTip = false

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);
Vue.use(auth);
Vue.use(LoadScript);

Vue.loadScript("https://kit.fontawesome.com/e168ca8cb0.js")

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  router,
  components: { App }
});