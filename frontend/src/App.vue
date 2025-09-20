<template>
  <div id="app">
    <AppHeader v-if="showHeader" />
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import AppHeader from './components/AppHeader.vue'
import { useRoute } from "vue-router";

const route = useRoute();
const showHeader = computed(() => {
  return !route?.meta?.hideHeader;
})
</script>

<style>
:root {
    --bulma-strong-weight: var(--bulma-weight-semibold);
    --bulma-menu-item-color: var(--bulma-white-ter);
    --bulma-menu-item-hover-color: var(--bulma-white-bis);
    --bulma-menu-item-hover-background-color: var(--bulma-grey-dark);
    --bulma-menu-item-active-color: var(--bulma-white);
    --bulma-menu-item-active-background-color: var(--bulma-purple);
    --bulma-menu-label-color: var(--bulma-grey-lighter);
}
</style>
