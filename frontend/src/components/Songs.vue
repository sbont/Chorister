<template>
    <div class="p-2">
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <DataTable :value="songs" size="small">
            <Column body-class="col-select" selection-mode="multiple"></Column>
            <Column body-class="col-no">
                <template #body="slotProps">
                    <span>
                        {{ oneBased(slotProps.index) }}
                    </span>
                </template>
            </Column>
            <Column header="Title" body-class="col-title">
                <template #body="slotProps">
                    <router-link :to="{ name: 'Song', params: { id: (slotProps.data as Song).id } }" append
                        class="has-text-weight-semibold">
                        {{ (slotProps.data as Song).title }}
                    </router-link>
                </template>
            </Column>
            <Column field="composer" header="Composer" body-class="col-composer"></Column>
            <Column field="lastEvent.date" header="Last Included" body-class="col-last-played"></Column>
            <Column header="Categories" body-class="col-category">
                <template #body="slotProps">
                    <div class="tags">
                        <span v-for="(category, index) in slotProps.data.categories?.resolved"
                            class="song-category tag is-normal" :key="index">
                            {{ category.name }}
                        </span>
                    </div>
                </template>
            </Column>
        </DataTable>

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
import DataTable from "primevue/datatable";
import Column from "primevue/column";


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

.col-select {
    width: 3%;
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