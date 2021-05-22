<template>
    <div class="columns">
      <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
        <RepertoireMenu />
      </div>
      <div class="column">
        <router-view></router-view>
      </div>
    </div>
</template>

<script>
import RepertoireMenu from '@/components/RepertoireMenu.vue'

// app Vue instance
const Repertoire = {
    name: "Repertoire",
    components: {
        RepertoireMenu
    }
};

export default Repertoire;
</script>

<style>
[v-cloak] {
    display: none;
}
</style>