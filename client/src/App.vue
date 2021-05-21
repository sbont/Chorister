<template>
  <div id="app">
    <Header />
    <div class="columns">
      <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
        <AppMenu />
      </div>
      <div class="column">
        <Repertoire />
      </div>
    </div>
  </div>
</template>

<script>
import Repertoire from './components/Repertoire.vue'
import Header from './components/AppHeader.vue'
import AppMenu from './components/AppMenu.vue'

export default {
  name: 'App',
  components: {
    Repertoire,
    Header,
    AppMenu
  },
  // app initial state
  data: () => {
    return {
    }
  }
}
</script>
