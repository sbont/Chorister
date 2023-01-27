<template>
  <div id="app">
    <Header v-if="showHeader" />
    <router-view></router-view>
  </div>
</template>

<script>
import Header from './components/AppHeader.vue'
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
    Header
  },

}
</script>
