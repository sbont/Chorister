<template>
    <div class="text">
        <div v-for="song in songs" :key="song.id">
            <div><b>{{ song.title }}</b></div>
            <div v-html="song.text" />
            <p></p>
        </div>
    </div>
</template>

<script lang="ts">
import {useSongs} from "@/stores/songStore";
import {useRoute} from "vue-router";
import {computed, onMounted, ref} from "vue";

export default {
    setup () {
        const songStore = useSongs();
        const route = useRoute();

        // state
        const text = ref("")
        const songs = ref([])

        onMounted(() => {
            const setlistId = route.params.id;
            songStore
                .getForSetlist(setlistId)
                .then(result => songs.value = result)
        })

        return { songs, text };
    },
}
</script>

<style>
    .text p {
        min-height: 18px;
    }
</style>