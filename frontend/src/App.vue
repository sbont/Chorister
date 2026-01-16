<template>
    <div id="app">
        <Toast />
        <ConfirmPopup group="popups"></ConfirmPopup>
        <ConfirmDialog group="dialogs"></ConfirmDialog>
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
import ConfirmPopup from 'primevue/confirmpopup';

const route = useRoute();
const showHeader = computed(() => {
    return !route?.meta?.hideHeader;
})
</script>
