<template>
    <div class="columns is-gapless">
        <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
            <RepertoireMenu />
        </div>
        <div class="column">
            <!-- this construction is necessary to rerender between different routes using the same view -->
            <router-view v-slot="{ Component, route }">
                <component :is="Component" :key="route.path" />
            </router-view>
        </div>
    </div>
</template>

<script setup lang="ts">
import RepertoireMenu from '@/components/RepertoireMenu.vue'

</script>

<style>
[v-cloak] {
    display: none;
}
</style>