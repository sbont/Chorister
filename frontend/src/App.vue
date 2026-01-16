<template>
    <div id="app">
        <Toast />
        <ConfirmDialog></ConfirmDialog>
        <AppHeader v-if="showHeader" />
        <router-view></router-view>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import AppHeader from './components/ui/AppHeader.vue'
import { useRoute } from "vue-router";
import Toast from 'primevue/toast';
import ConfirmDialog from 'primevue/confirmdialog';

const route = useRoute();
const showHeader = computed(() => {
    return !route?.meta?.hideHeader;
})
</script>
