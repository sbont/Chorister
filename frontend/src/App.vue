<template>
  <div id="app">
    <AppHeader v-if="showHeader" />
    <router-view></router-view>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue'
import {useRoute} from "vue-router";

export default {
  name: 'App',
    computed: {
      showHeader: () => {
          let route = useRoute();
          return !route.meta.hideHeader;
      }
    },
  components: {
    AppHeader
  },

}
</script>
