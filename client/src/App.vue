<template>
  <div id="app">
    <Header />
    <router-view></router-view>
  </div>
</template>

<script>
import Header from './components/AppHeader.vue'

export default {
  name: 'App',
  components: {
    Header
  }
}
</script>
