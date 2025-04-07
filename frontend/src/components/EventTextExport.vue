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
import { Song } from "@/entities/song";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";

const eventStore = useEvents();
const route = useRoute();

// state
const { entries } = storeToRefs(eventStore);
const songs = ref<Array<Song>>([])
const eventId = Number(route.params.id);
eventStore.fetch(eventId)
    .then(result => {
        songs.value = entries.value(result.uri)
    })

</script>

<style>
.text p {
    min-height: 18px;
}
</style>