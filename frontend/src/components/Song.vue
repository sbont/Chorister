<template>
    <div class="song">
        <progress
            v-if="loading"
            class="progress is-medium is-info"
            max="100"
        ></progress>

        <div v-if="!loading">
            <section class="hero is-info">
                <div v-if="!editing" class="hero-body">
                    <p class="title">{{ song.title }}</p>
                    <p class="subtitle">
                        {{ song.composer }} | {{ song.songbook.title }}
                        {{ song.number }}
                    </p>
                </div>
                <div v-else class="hero-body">
                    <p class="title">{{ isNew ? "Add new" : "Edit" }}</p>
                    <p class="subtitle">song</p>
                </div>
            </section>

            <div class="song-info m-2 columns">
                <div class="column">
                    <div>
                        <div class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Title</label>
                            </div>
                            <div class="field-body">
                                <div
                                    class="field"
                                    v-bind:class="{ static: !editing }"
                                >
                                    <div class="control">
                                        <input
                                            v-if="editing"
                                            v-model="draftValues.title"
                                            class="input"
                                            type="text"
                                            placeholder="The name of the song or hymn"
                                        />
                                        <span v-else>
                                            {{ song.title }}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Composer</label>
                            </div>
                            <div class="field-body">
                                <div
                                    class="field"
                                    v-bind:class="{ static: !editing }"
                                >
                                    <div class="control">
                                        <input
                                            v-if="editing"
                                            v-model="draftValues.composer"
                                            class="input"
                                            type="text"
                                            placeholder="Artist / composer / writer"
                                        />
                                        <span v-else>
                                            {{ song.composer }}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="field is-horizontal mb-3">
                            <div class="field-label is-normal">
                                <label class="label">Songbook</label>
                            </div>
                            <div class="field-body field-flex">
                                <div class="field-flex-col">
                                    <div
                                        class="field"
                                        v-bind:class="{ static: !editing }"
                                    >
                                        <div class="control">
                                            <input
                                                v-if="editing"
                                                v-model="
                                                    draftValues.songbook.title
                                                "
                                                class="input"
                                                type="text"
                                                placeholder="Songbook, hymnal or collection title"
                                            />
                                            <span v-else>
                                                {{ song.songbook.title }}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="field-flex-col is-one-third ml-5">
                                    <div class="field is-horizontal">
                                        <div class="field-label is-normal">
                                            <label class="label">Number</label>
                                        </div>
                                        <div class="field-body">
                                            <div
                                                class="field"
                                                v-bind:class="{
                                                    static: !editing,
                                                }"
                                            >
                                                <div class="control">
                                                    <input
                                                        v-if="editing"
                                                        v-model="
                                                            draftValues.songbookNumber
                                                        "
                                                        class="input"
                                                        type="text"
                                                        placeholder="Song number in the book"
                                                    />
                                                    <span v-else>
                                                        {{
                                                            song.songbookNumber
                                                        }}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div v-if="editing" class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Youtube video</label>
                            </div>
                            <div class="field-body">
                                <div
                                    class="field"
                                    v-bind:class="{ static: !editing }"
                                >
                                    <div class="control">
                                        <input
                                            v-model="draftValues.recordingUrl"
                                            class="input"
                                            type="url"
                                            placeholder="https://www.youtube.com/watch?v=..."
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div>
                        <div class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Time of the year</label>
                            </div>
                            <div class="field-body">
                                <div 
                                    class="field"
                                    v-bind:class="{ static: !editing }"
                                >
                                    <div class="control">
                                        <div class="select is-multiple">
                                            <multiselect 
                                                v-if="editing"
                                                v-model="draftSongCategories.season" 
                                                :multiple="true"
                                                :options="categories.season"
                                                track-by="name"
                                                label="name"
                                                :close-on-select="false"
                                            ></multiselect>
                                                        
                                            <div v-else class="tags are-medium">
                                                <span 
                                                    v-for="(category) in songCategories.season" 
                                                    :key="category.id"
                                                    class="tag"
                                                >
                                                    {{ category.name }}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Liturgical place</label>
                            </div>
                            <div class="field-body">
                                <div 
                                    class="field"
                                    v-bind:class="{ static: !editing }"
                                >
                                    <div class="control">
                                        <div class="select is-multiple">
                                            <multiselect 
                                                v-if="editing"
                                                v-model="draftSongCategories.liturgical" 
                                                :multiple="true"
                                                :options="categories.liturgical"
                                                track-by="name"
                                                label="name"
                                                :close-on-select="false"
                                            ></multiselect>
                                                        
                                            <div v-else class="tags are-medium">
                                                <span 
                                                    v-for="(category) in songCategories.liturgical" 
                                                    :key="category.id"
                                                    class="tag"
                                                >
                                                    {{ category.name }}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="field is-grouped mt-5">
                        <p v-if="!editing" class="control">
                            <button 
                                @click="edit" 
                                class="button is-link"
                            >
                                Edit
                            </button>
                        </p>
                        <p v-if="!editing" class="control">
                            <button
                                @click="remove"
                                class="button is-danger"
                            >
                                Delete
                            </button>
                        </p>
                        <p v-if="editing" class="control">
                            <button
                                @click="save"
                                class="button is-link"
                                :class="{ 'is-loading': saving }"
                            >
                                Save changes
                            </button>
                        </p>
                        <p v-if="editing" class="control">
                            <button @click="cancelEdit" class="button">
                                Cancel
                            </button>
                        </p>
                    </div>
                </div>

                <div class="column">
                    <iframe
                        v-if="youtubeId"
                        id="ytplayer"
                        type="text/html"
                        width="640"
                        height="360"
                        :src="
                            'https://www.youtube.com/embed/' +
                            youtubeId +
                            '?autoplay=0'
                        "
                        frameborder="0"
                    ></iframe>
                </div>
            </div>

            <div class="scores p-3">
                <div class="is-size-4">Scores</div>
                <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
                    <Score v-for="score in scores" :key="score.id" :score="score" @remove="removeScore(score)"></Score>
                    <div class="m-2">
                        <button class="button is-primary" @click="addScore">
                            Add
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>

import api from "../api";
import Score from "@/components/Score.vue"
import Multiselect from 'vue-multiselect'
import { Log } from "oidc-client";

// app Vue instance
const Song = {
    name: "Song",

    // app initial state
    data: function () {
        return {
            song: {},
            songCategories: null,
            scores: [],
            categories: {},
            editing: false,
            draftValues: null,
            draftSongCategories: null,
            loading: true,
            saving: false,
            error: null,
        };
    },

    mounted: function () {
        let categoriesLoaded = api.getAllCategories()
            .then(response => {
                this.$log.debug("Categories loaded: ", response.data);
                this.categories.season = response.data.filter(category => category.type === "SEASON");
                this.categories.liturgical = response.data.filter(category => category.type === "LITURGICAL_MOMENT");
            }) 
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load categories";
            });
        if (this.$route.name === "NewSong") {
            this.draftValues = {
                songbook: {},
            };
            this.draftSongCategories = {
                season: [],
                liturgical: []
            }
            this.editing = true;
            categoriesLoaded.finally(() => this.loading = false);
        } else {
            var songId = this.$route.params.id;
            let songLoaded = api.getSongById(songId)
                .then((response) => {
                    this.$log.debug("Song loaded: ", response.data);
                    this.song = response.data;
                    if (!this.song.songbook) {
                        this.song.songbook = {};
                    }
                    api.getScoresBySongId(this.song.id)
                        .then((response) => {
                            this.$log.debug("Scores loaded: ", response.data);
                            this.scores = response.data;
                        })
                        .catch((error) => {
                            this.$log.debug(error);
                            this.error = "Failed to load scores";
                        });
                })
                .catch((error) => {
                    this.$log.debug(error);
                    this.error = "Failed to load song";
                    this.loading = false;
                });
            let songCategoriesLoaded = api.getSongCategories(songId)
                .catch((error) => {
                    this.$log.debug(error);
                    this.error = "Failed to load song categories";
                    this.loading = false;
                });
            Promise.all([songLoaded, categoriesLoaded, songCategoriesLoaded])
                .then(([songResponse, categoryResponse, songCategoryResponse]) => {
                    this.songCategories = {};
                    this.songCategories.season = this.categories.season.filter(category => songCategoryResponse.data.some(songCategory => category._links.self.href === songCategory._links.self.href));
                    this.songCategories.liturgical = this.categories.liturgical.filter(category => songCategoryResponse.data.some(songCategory => category._links.self.href === songCategory._links.self.href));
                })
                .finally(() => (this.loading = false));
        }
    },

    computed: {
        isNew: function () {
            return !this.song.id;
        },
        youtubeId: function () {
            return this.song.recordingUrl
                ? this.song.recordingUrl.split("?v=")[1]
                : null;
        },
    },

    methods: {
        save: function () {
            this.saving = true;
            var song = this.draftValues;
            if (!song) {
                return;
            }
            const isNew = !song.id;
            const promise = this.saveToServer(song);
            promise.then((response) => {
                if(isNew) {
                    song.id = response.data.id;
                }
                let previousSongCategories = this.songCategories ? this.songCategories.season.concat(this.songCategories.liturgical) : [];
                let draftSongCategories = this.draftSongCategories.season.concat(this.draftSongCategories.liturgical);
                let newSongCategories = draftSongCategories.filter(draftSongCategory => !previousSongCategories.some(previousSongCategory => draftSongCategory._links.self.href === previousSongCategory._links.self.href));
                let deletedSongCategories = previousSongCategories.filter(previousSongCategory => !draftSongCategories.some(draftSongCategory => previousSongCategory._links.self.href === draftSongCategory._links.self.href));
                if(newSongCategories.length) {
                    api.postSongCategories(song.id, newSongCategories.map(songCategory => songCategory._links.self.href));
                }
                deletedSongCategories.forEach(songCategory => api.deleteSongCategory(song.id, songCategory.id));
                this.editing = false;
                this.saving = false;
                this.song = song;
                this.songCategories = this.draftSongCategories;
                this.draftValues = null;
                this.draftSongCategories = [];
                if(isNew) {
                    this.$router.push({
                        name: "Song",
                        params: { id: song.id },
                    });
                } 
            })
        },

        saveToServer: function(song) {
            if (song.id) {
                return api.updateSongForId(song.id, song);
            } else {
                return api.createNewSong(song);
            }
        },

        remove: function () {
            api.deleteSongForId(this.song.id).then((response) => {
                this.$router.push({ name: "Repertoire" });
            });
        },

        edit: function () {
            this.draftValues = this.song;
            this.draftSongCategories = Object.assign({}, this.songCategories);
            this.editing = true;
        },

        cancelEdit: function () {
            this.draftValues = null;
            this.draftSongCategories = null;
            this.editing = false;
        },

        addScore: function () {
            var newScore = {
                id: null,
                song: this.song._links.self.href
            };
            this.scores.push(newScore);
        },

        removeScore: function (score) {
            if(score.id) {
                api.deleteScoreForId(score.id);
            }
            this.scores = this.scores.filter(current => {current.id !== score.id});
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

    components: {
        Score,
        Multiselect
    }
};

export default Song;
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>

<style>
[v-cloak] {
    display: none;
}
</style>