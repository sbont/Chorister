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
                    <p class="title">{{ isNew ? 'Add new' : 'Edit' }}</p>
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
                                                v-model="draftValues.songbook.title"
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
                                <div class="field" v-bind:class="{ static: !editing }">
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
                                <div class="tags are-medium">
                                    <span class="tag">Christmas</span>
                                    <span class="tag">Advent</span>
                                </div>
                            </div>
                        </div>
                        <div class="field is-horizontal">
                            <div class="field-label is-normal">
                                <label class="label">Liturgical place</label>
                            </div>
                            <div class="field-body">
                                <div class="tags are-medium">
                                    <span class="tag">Gloria</span>
                                    <span class="tag">Communion</span>
                                    <span class="tag">Outro</span>
                                </div>
                            </div>
                        </div>
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

            <div class="field is-grouped">
                <p v-if="!editing" class="control">
                    <button @click="edit" class="button is-link">Edit</button>
                </p>
                <p v-if="!editing" class="control">
                    <button @click="deleteSong" class="button is-danger">
                        Delete
                    </button>
                </p>
                <p v-if="editing" @click="edit" class="control">
                    <button @click="save" class="button is-link" :class="{'is-loading' : saving}">
                        Save changes
                    </button>
                </p>
                <p v-if="editing" class="control">
                    <button @click="cancelEdit" class="button">Cancel</button>
                </p>
            </div>
        </div>

        <!-- <label @dblclick="edit">{{ song.title }}</label>
        <button class="destroy" @click="removeSong(song)"></button>

        <input
            class="edit"
            type="text"
            v-model="song.title"
            v-song-focus="song == editedSong"
            @blur="doneEdit(song)"
            @keyup.enter="doneEdit(song)"
            @keyup.esc="cancelEdit(song)"
        /> -->

        <!-- <input
                class="new-song-title"
                autofocus
                autocomplete="off"
                :placeholder="this.inputPlaceholder"
                v-model="newSong"
                @keyup.enter="addSong"
            /> -->
    </div>
</template>
<script>
import api from "../Api";

// app Vue instance
const Song = {
    name: "Song",

    // app initial state
    data: function () {
        return {
            song: {},
            editing: false,
            draftValues: null,
            loading: true,
            saving: false,
            error: null,
        };
    },

    mounted: function () {
        if (this.$route.name === "NewSong") {
            this.draftValues = {
                songbook: {}
            };
            this.editing = true;
            this.loading = false;
        } else {
            var songId = this.$route.params.id;
            api.getById(songId)
                .then((response) => {
                    this.$log.debug("Song loaded: ", response.data);
                    this.song = response.data;
                    if(!this.song.songbook) {
                        this.song.songbook = {};
                    }
                })
                .catch((error) => {
                    this.$log.debug(error);
                    this.error = "Failed to load song";
                })
                .finally(() => (this.loading = false));
        }
    },

    computed: {
        isNew: function() {
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
            if (song.id) {
                api.updateForId(song.id, song)
                    .then((response) => {
                        this.editing = false;
                        this.saving = false;
                        this.draftValues = null;
                    });
            } else {
                api.createNew(song)
                    .then((response) => {
                        console.log(response);
                        song.id = response.data.id;
                        this.editing = false;
                        this.saving = false;
                        this.draftValues = null;
                        this.$router.push({ name: 'Song', params : { id: song.id }});
                    });
            }
        },

        deleteSong: function () {
            api.deleteForId(this.song.id)
                    .then((response) => {
                        this.$router.push({ name: 'Repertoire' });
                    });
        },

        edit: function () {
            this.draftValues = this.song;
            this.editing = true;
        },

        doneEdit: function (song) {
            if (!this.draftValues) {
                return;
            }

            this.editedSong = null;
            song.title = song.title.trim();

            if (!song.title) {
                this.removeSong(song);
            }
        },

        cancelEdit: function () {
            this.draftValues = null;
            this.editing = false;
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

export default Song;
</script>

<style>
[v-cloak] {
    display: none;
}
</style>