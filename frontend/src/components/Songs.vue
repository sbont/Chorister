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
            ERROR: {{ this.error }}
        </div>
        <table
            class="table is-hoverable is-fullwidth"
            v-if="!loading"
            v-cloak
        >
            <thead>
                <th title="number"></th>
                <th>Title</th>
                <th>Composer</th>
                <th>Songbook</th>
                <th>No.</th>
                <th># Scores</th>
                <th>Last Played</th>
                <th>Categories</th>
            </thead>
            <tbody>
                <tr v-for="(song, index) in songs" class="song" :key="song.id">
                    <td>{{ oneBased(index) }}</td>
                    <th><router-link :to="{ name: 'Song', params: { id: song.id }}" append>{{ song.title }}</router-link></th>
                    <td>{{ song.composer }}</td>
                    <td>{{ (song.songbook || {}).title }}</td>
                    <td>{{ song.songbookNumber }}</td>
                    <td>{{ song.id }}</td>
                    <td>...</td>
                    <td>...</td>
                </tr>
            </tbody>
            <tfoot></tfoot>
        </table>
        <footer class="footer" v-cloak>
                <strong>{{ songs.length }}</strong>
                {{
                    songs | pluralize
                }}
        </footer>
    </div>
</template>

<script>
import api from "../api";
import { Dropdown } from 'buefy'

const Songs = {
    name: "Songs",
    props: {
        activeUser: Object,
    },

    // app initial state
    data: function () {
        return {
            songs: [],
            newSong: "",
            editedSong: null,
            loading: true,
            error: null,
            name: "Steven",
        };
    },

    mounted: function () {
        console.log(this.$route.params);
        const routeName = this.$route.name;
        let songsLoaded;
        switch(routeName) {
            case 'Repertoire':
                songsLoaded = api.getAllSongs();
                break;
            case 'CategorySeason':
            case 'CategoryLiturgical':
                var categoryId = this.$route.params.id;
                songsLoaded = api.getSongsByCategoryId(categoryId);
                break;
            case 'Setlist':
                var setlistId = this.$route.params.id;
                songsLoaded = api.getSetlistSongs(setlistId);
                break;
        }
        songsLoaded
            .then((response) => {
                this.$log.debug("Data loaded: ", response.data);
                this.songs = response.data;
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load repertoire";
            })
            .finally(() => (this.loading = false));
    },

    // computed properties
    // http://vuejs.org/guide/computed.html
    computed: {
        userEmail: function () {
            return this.activeUser ? this.activeUser.email : "";
        },
        inputPlaceholder: function () {
            return this.activeUser
                ? this.activeUser.given_name + ", what needs to be sung?"
                : "What needs to be sung?";
        },
    },

    filters: {
        pluralize: function (n) {
            return n === 1 ? "song" : "songs";
        },
    },

    // methods that implement data logic.
    // note there's no DOM manipulation here at all.
    methods: {
        addSong: function () {
            var value = this.newSong && this.newSong.trim();
            if (!value) {
                return;
            }

            this.songs.push({
                title: value,
                author: "Dirk",
            });

            this.newSong = "";
        },

        removeSong: function (song) {
            // notice NOT using "=>" syntax
            this.songs.splice(this.songs.indexOf(song), 1);
        },

        editSong: function (song) {
            this.beforeEditCache = song.title;
            this.editedSong = song;
        },

        doneEdit: function (song) {
            if (!this.editedSong) {
                return;
            }

            this.editedSong = null;
            song.title = song.title.trim();

            if (!song.title) {
                this.removeSong(song);
            }
        },

        cancelEdit: function (song) {
            this.editedSong = null;
            song.title = this.beforeEditCache;
        },

        handleErrorClick: function () {
            this.error = null;
        },

        oneBased(index) {
            return index + 1;
        },
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
    },
};

export default Songs;
</script>

<style>
[v-cloak] {
    display: none;
}
</style>