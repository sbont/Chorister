<template>    
    <div class="p-2">
        <!-- <div class="is-flex is-justify-content-space-between">
            <h1 class="title">Repertoire</h1>
            <router-link class="button is-primary" to="song/new" append tag="button">Add +</router-link>
        </div> -->
        <progress
            v-if="loading"
            class="progress is-medium is-info"
            max="100"
        ></progress>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error.value }}
        </div>
        <table
            class="table is-hoverable is-fullwidth song-table"
            v-if="!loading"
            v-cloak
        >
            <thead>
                <th class="col1" title="number"></th>
                <th class="col2">Title</th>
                <th class="col3">Composer</th>
                <th class="col4">Songbook</th>
                <th class="col5">No.</th>
                <th class="col6"># Scores</th>
                <th class="col7">Last Played</th>
                <th class="category-col">Categories</th>
                <th v-if="!!setlistId"></th>
            </thead>
            <tbody>
                <tr v-for="(song, index) in songs" class="song" :key="song.id" draggable="true" @dragstart="startDrag($event, song)">
                    <td>{{ oneBased(index) }}</td>
                    <th><router-link :to="{ name: 'Song', params: { id: song.id }}" append>{{ song.title }}</router-link></th>
                    <td>{{ song.composer }}</td>
                    <td>{{ (song.songbook || {}).title }}</td>
                    <td>{{ song.songbookNumber }}</td>
                    <td>{{ song.scores.length }}</td>
                    <td>{{ song.lastSetlist?.date }}</td>
                    <td class="category-col">
                        <div class="tags">
                            <span v-for="(category, index) in song.categories" class="song-category tag is-normal" :key="index">
                                {{ category.name }}
                            </span>
                        </div>
                    </td>
                    <td v-if="!!setlistId" class="p-1b">
                        <button class="button is-danger is-inverted is-small" :class="{ 'is-loading': song.deleting }" @click="deleteSong(song)">
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
                {{ pluralize(songs) }}
        </footer>
    </div>
</template>

<script lang="ts">
import api from "./../api.js";
import { computed, onMounted, ref } from 'vue'
import { useSongs } from "@/stores/songStore";
import { useRoute, useRouter } from "vue-router";
import { storeToRefs } from "pinia";


export default {
    props: {
        activeUser: Object
    },
    emits: ["remove"],
    setup(props, { emit }) {
        const songStore = useSongs();
        const route = useRoute();
        const router = useRouter();

        // state
        const activeUser = ref(props.activeUser);
        const songs = ref([]);
        const newSong = ref();
        const editedSong = ref(null);
        const loading = ref(true);
        const error = ref(null);
        const setlistId = ref(null);

        let beforeEditCache = null;

        // Computed
        const userEmail = computed(() => activeUser.value ? activeUser.value.email : "");
        const inputPlaceholder = computed(() => activeUser.value ? activeUser.value.given_name + ", what needs to be sung?" : "What needs to be sung?")

        onMounted(() => {
            loadSongs();
        });

        // Methods
        const pluralize = function (n) {
            return n === 1 ? "song" : "songs";
        }
        
        const loadSongs = function() {
            const routeName = route.name;
            let id = route.params.id;
            let songsLoaded;
            let transformer;
            let sortTransformer = data => data.sort((songA, songB) => songA.title.localeCompare(songB.title));
            switch(routeName) {
                case 'Repertoire':
                    songsLoaded = api.getAllSongs();
                    transformer = sortTransformer;
                    break;
                case 'CategorySeason':
                case 'CategoryLiturgical':
                    songsLoaded = api.getSongsByCategoryId(id);
                    transformer = sortTransformer;
                    break;
                case 'Setlist':
                    setlistId.value = id;
                    songsLoaded = api.getSetlistEntries(id);
                    transformer = entries => {
                        let sorted = entries.sort((entryA, entryB) => entryA.number - entryB.number);
                        return sorted.map(entry => Object.assign(entry._embedded.song, { setlistEntryUri: entry._links.self.href}));
                    }
                    break;
            }
            songsLoaded
                .then((response) => {
                    console.log("Data loaded: ", response.data);
                    songs.value = transformer(response.data);
                })
                .catch((error) => {
                    console.error(error);
                    error.value = "Failed to load songs";
                })
                .finally(() => (loading.value = false));
        }

        const addSong = function () {
            var value = newSong.value && newSong.value.trim();
            if (!value) {
                return;
            }

            songs.value.push({
                title: value,
                author: "Dirk",
            });

            newSong.value = "";
        }

        const removeSong = function (song) {
            // notice NOT using "=>" syntax
            songs.value.splice(songs.value.indexOf(song), 1);
        }

        const editSong = function (song) {
            beforeEditCache = song.title;
            editedSong.value = song;
        }

        const doneEdit = function (song) {
            if (!editedSong.value) {
                return;
            }

            editedSong.value = null;
            song.title = song.title.trim();

            if (!song.title) {
                removeSong(song);
            }
        }

        const cancelEdit = function (song) {
            editedSong.value = null;
            song.title = beforeEditCache;
        }

        const handleErrorClick = function () {
            error.value = null;
        }

        const oneBased = (index) => index + 1;

        const startDrag = function(event, song) {
            event.dataTransfer.dropEffect = "link";
            event.dataTransfer.setData("text/plain", song._links.self.href);
        }

        const deleteSong = function(song) {
            loading.value = true;
            song.deleting = true;
            let entryUri = song.setlistEntryUri;
            api.delete(entryUri)
                .then((response) => {
                    console.log("Entry removed: " + entryUri);
                    removeSong(song);
                })
                .catch((error) => {
                    console.error(error);
                    error.value = "Failed to remove entry";
                })
                .finally(() => loading.value = false);
        }

        return {
            songs, error, loading, setlistId,
            userEmail, inputPlaceholder,
            pluralize, loadSongs, addSong, removeSong, editSong, doneEdit, cancelEdit, handleErrorClick, oneBased, startDrag, deleteSong                
        }
    },

    // a custom directive to wait for the DOM to be updated
    // before focusing on the input field.
    // http://vuejs.org/guide/custom-directive.html
    directives: {
        "song-focus": function (el, binding) {
            if (binding.value) {
                el.focus();
            }
        },
    }
};
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