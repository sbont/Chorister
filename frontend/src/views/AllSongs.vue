<template>
    <div>
        <div class="is-flex is-justify-content-space-between m-3">
            <h1 class="title">All Songs</h1>
            <router-link class="button is-primary" :to="{ name: 'NewSong' }"  append tag="button">
                <span class="icon is-small">
                    <i class="fas fa-plus"></i>
                </span>
                <span>Add</span>
            </router-link>
        </div>
        <Songs />
    </div>
</template>

<script setup lang="ts">
import Songs from '@/components/Songs.vue'

</script>