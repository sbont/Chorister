<template>
    <div class="p-2">
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <table class="table is-hoverable is-fullwidth song-table" v-cloak>
            <thead>
                <tr>
                    <th class="col-no" title="number"></th>
                    <th class="col-title">Title</th>
                    <th class="col-composer">Composer</th>
                    <th class="col-last-played">Last Played</th>
                    <th class="col-category">Categories</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(song, index) in songs" class="song" :key="song.id" draggable="true"
                    @dragstart="startDrag($event, song)">
                    <td>{{ oneBased(index) }}</td>
                    <th>
                        <router-link :to="{ name: 'Song', params: { id: song.id } }" append>{{ song.title
                            }}</router-link>
                    </th>
                    <td>{{ song.composer }}</td>
                    <td>{{ song.lastEvent?.date }}</td>
                    <td class="col-category">
                        <div class="tags">
                            <span v-for="(category, index) in song.categories?.resolved"
                                class="song-category tag is-normal" :key="index">
                                {{ category.name }}
                            </span>
                        </div>
                    </td>
                </tr>
            </tbody>
            <tfoot></tfoot>
        </table>
        <progress v-if="loading" class="progress is-medium is-info" max="100"></progress>
        <footer class="footer" v-cloak>
            <strong>{{ songs.length }}</strong>
            {{ pluralize(songs.length) }}
        </footer>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSongs } from "@/application/songStore.js";
import { useRoute } from "vue-router";
import { Song } from "@/entities/song";

// Types
const songStore = useSongs();
const { fetchAll, fetchAllForCategory, allSongs } = songStore;
const route = useRoute();

// state
const songs = ref<Array<Song>>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const routeName = route.name;
let id = Number(route.params.id);
let songsLoaded;
switch (routeName) {
    case "Repertoire":
        songsLoaded = fetchAll();
        songs.value = allSongs;
        if (songs.value.length)
            loading.value = false;

        break;
    case "Category":
        songsLoaded = fetchAllForCategory(id);
        break;
}
songsLoaded!
    .then(data => {
        songs.value = data;
    })
    .finally(() => (loading.value = false));


// Methods
const pluralize = function (n: number) {
    return n === 1 ? "song" : "songs";
}

const handleErrorClick = function () {
    error.value = null;
}

const oneBased = (index: number) => index + 1;

const startDrag = function (event: DragEvent, song: Song) {
    if (event.dataTransfer) {
        event.dataTransfer.dropEffect = "link";
        event.dataTransfer.setData("text/plain", song.uri!);
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

.col-no {
    width: 3%;
}

.col-title {
    width: 25%;
}

.col-composer {
    width: 20%;
}

.col-songbook {
    width: 15%;
}

.col-songbook-no {
    width: 5%;
}

.col-last-played {
    width: 10%;
}

.col-category {
    width: 32%;
}

.col-category .tags {
    flex-wrap: initial;
}
</style>