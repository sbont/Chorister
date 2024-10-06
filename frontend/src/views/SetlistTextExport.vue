<template>
    <div class="text">
        <div v-for="song in songs" :key="song.id">
            <div><b>{{ song.title }}</b></div>
            <div v-html="song.text"></div>
            <p></p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { ref } from "vue";
import { Song } from "@/types";
import { useSetlists } from "@/stores/setlistStore";

const setlistStore = useSetlists();
const route = useRoute();

// state
const songs = ref<Array<Song>>([])
const setlistId = Number(route.params.id);
setlistStore
    .fetchEntries(setlistId)
    .then(result => {
        console.log(result)
        songs.value = result.map(e => e._embedded.song)
    })

</script>

<style>
.text p {
    min-height: 18px;
}
</style>