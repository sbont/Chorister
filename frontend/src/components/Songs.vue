<template>
    <div class="p-2">
        <progress v-if="loading" class="progress is-medium is-info" max="100"></progress>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <table class="table is-hoverable is-fullwidth song-table" v-if="!loading" v-cloak>
            <thead>
            <tr>
                <th class="col1" title="number"></th>
                <th class="col2">Title</th>
                <th class="col3">Composer</th>
                <th class="col4">Songbook</th>
                <th class="col5">No.</th>
                <th class="col7">Last Played</th>
                <th class="category-col">Categories</th>
            </tr>
            </thead>
            <tbody>
                <tr v-for="(song, index) in songs" class="song" :key="song.id" draggable="true"
                    @dragstart="startDrag($event, song)">
                    <td>{{ oneBased(index) }}</td>
                    <th><router-link :to="{ name: 'Song', params: { id: song.id } }" append>{{ song.title }}</router-link>
                    </th>
                    <td>{{ song.composer }}</td>
                    <td>{{ (song.songbook || {}).title }}</td>
                    <td>{{ song.songbookNumber }}</td>
                    <td>{{ song.lastSetlist?.date }}</td>
                    <td class="category-col">
                        <div class="tags">
                            <span v-for="(category, index) in song.categories" class="song-category tag is-normal"
                                :key="index">
                                {{ category.name }}
                            </span>
                        </div>
                    </td>
                </tr>
            </tbody>
            <tfoot></tfoot>
        </table>
        <footer class="footer" v-cloak>
            <strong>{{ songs.length }}</strong>
            {{ pluralize(songs.length) }}
        </footer>
    </div>
</template>

<script setup lang="ts">
import api from "./../api.js";
import { onMounted, ref } from 'vue'
import { useSongs } from "@/stores/songStore";
import { useRoute, useRouter } from "vue-router";
import { SetlistEntry, Song, User, WithEmbedded } from "@/types";

// Types

type DraftSong = Partial<Song>

const emit = defineEmits(["remove"])

const songStore = useSongs();
const route = useRoute();
const router = useRouter();

// state
const songs = ref<Array<Song>>([]);
const loading = ref(true);
const deleting = ref(false)
const error = ref<string | null>(null);
const setlistId = ref<number>();

onMounted(() => {
    loadSongs();
});

// Methods
const pluralize = function (n: number) {
    return n === 1 ? "song" : "songs";
}

const loadSongs = function () {
    const sorter = (data: Array<Song>) => data.sort((songA, songB) => songA.title.localeCompare(songB.title));
    const routeName = route.name;
    let id = Number(route.params.id);
    let songsLoaded;
    switch (routeName) {
        case 'Repertoire':
            songsLoaded = songStore.fetchAll().then(response => songs.value = sorter(response.data));
            break;
        case 'CategorySeason':
        case 'CategoryLiturgical':
            songsLoaded = api.getSongsByCategoryId(id).then(response => songs.value = sorter(response.data));
            break;
    }
    songsLoaded!.finally(() => (loading.value = false));
}

const handleErrorClick = function () {
    error.value = null;
}

const oneBased = (index: number) => index + 1;

const startDrag = function (event: DragEvent, song: Song) {
    if (event.dataTransfer) {
        event.dataTransfer.dropEffect = "link";
        event.dataTransfer.setData("text/plain", song._links!.self.href);
    }
}

</script>

<style>
[v-cloak] {
    display: none;
}

.song-table {
    table-layout: fixed;
}

td.p-1b {
    padding: 0.3em;
}

.col1 {
    width: 3%;
}

.col2 {
    width: 20%;
}

.col3 {
    width: 15%;
}

.col4 {
    width: 15%;
}

.col5 {
    width: 5%;
}

.col6 {
    width: 10%;
}

.col7 {
    width: 10%;
}

.category-col {
    width: 22%;
}

.category-col .tags {
    flex-wrap: initial;
}
</style>