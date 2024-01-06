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
