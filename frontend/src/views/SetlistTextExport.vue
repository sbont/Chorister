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
import { useSongs } from "@/stores/songStore";
import { useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { Song } from "@/types";

const songStore = useSongs();
const route = useRoute();

// state
const text = ref("")
const songs = ref<Array<Song>>([])

onMounted(() => {
    const setlistId = Number(route.params.id);
    songStore
        .getForSetlist(setlistId)
        .then(result => songs.value = result)
})

</script>

<style>
.text p {
    min-height: 18px;
}
</style>