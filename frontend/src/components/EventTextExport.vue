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
import { useEvents } from "@/stores/eventStore";

const eventStore = useEvents();
const route = useRoute();

// state
const songs = ref<Array<Song>>([])
const eventId = Number(route.params.id);
eventStore
    .fetchEntries(eventId)
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