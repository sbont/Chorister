<template>
    <div class="p-2">
        <!-- <div class="is-flex is-justify-content-space-between">
            <h1 class="title">Repertoire</h1>
            <router-link class="button is-primary" to="song/new" append tag="button">Add +</router-link>
        </div> -->
        <progress v-if="loading" class="progress is-medium is-info" max="100"></progress>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <table class="table is-hoverable is-fullwidth song-table" v-if="!loading" v-cloak>
            <thead>
                <th class="col1" title="number"></th>
                <th class="col2">Title</th>
                <th class="col3">Composer</th>
                <th class="col4">Songbook</th>
                <th class="col5">No.</th>
                <th class="col7">Last Played</th>
                <th class="category-col">Categories</th>
                <th v-if="!!setlistId"></th>
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
                    <td v-if="!!setlistId" class="p-1b">
                        <button class="button is-danger is-inverted is-small" :class="{ 'is-loading': deleting }"
                            @click="removeSongFromSetlist(song as SetlistSong)">
                            <span class="icon is-small">
                                <i class="fas fa-times"></i>
                            </span>
                        </button>
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
interface SetlistSong extends Song {
    setlistEntryUri: string
}

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
    const setlistExtractor = (entries: Array<SetlistEntry & WithEmbedded<"song", Song>>) => {
        let sorted = entries.sort((entryA, entryB) => entryA.number - entryB.number);
        return sorted.map(entry => Object.assign(entry._embedded.song, { setlistEntryUri: entry?._links?.self.href })) as Array<SetlistSong>;
    }

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
        case 'Setlist':
            setlistId.value = id;
            songsLoaded = api.getSetlistEntries(id).then(response => songs.value = setlistExtractor(response.data));
            break;
    }
    songsLoaded!.finally(() => (loading.value = false));
}

const removeSong = function (song: Song) {
    // notice NOT using "=>" syntax
    songs.value.splice(songs.value.indexOf(song), 1);
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

const removeSongFromSetlist = function (song: SetlistSong) {
    loading.value = true;
    deleting.value = true;
    let entryUri = song.setlistEntryUri;
    api.deleteByUri(entryUri)
        .then((response) => {
            removeSong(song);
        })
        .catch((error) => {
            error.value = "Failed to remove entry";
        })
        .finally(() => {
            loading.value = false;
            deleting.value = false;
        });
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