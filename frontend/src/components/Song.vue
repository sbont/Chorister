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
                                            <VueMultiselect
                                                v-if="editing"
                                                v-model="draftSongCategories.season"
                                                :multiple="true"
                                                :options="categories.season"
                                                track-by="name"
                                                label="name"
                                                :close-on-select="false"
                                            ></VueMultiselect>
                                                        
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
                                            <VueMultiselect
                                                v-if="editing"
                                                v-model="draftSongCategories.liturgical"
                                                :multiple="true"
                                                :options="categories.liturgical"
                                                track-by="name"
                                                label="name"
                                                :close-on-select="false"
                                            ></VueMultiselect>

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
import VueMultiselect from 'vue-multiselect'
import {computed, onMounted, ref } from 'vue' // or '@vue/composition-api' in Vue 2.x
import { useVuelidate } from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import { useSongs } from "@/stores/songStore";
import { useCategories } from "@/stores/categoryStore";
import { useScores } from "@/stores/scoreStore";
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";

export default {
    setup () {
        const songStore = useSongs();
        const categoryStore = useCategories();
        const scoreStore = useScores();
        const route = useRoute();
        const router = useRouter()

        /*const state = reactive({})
        const rules = {}

        const v$ = useVuelidate(rules, state)

        return { state, v$ }*/

        // state
        const { categories } = storeToRefs(categoryStore);
        const song = ref({});
        const songCategories = ref();
        const scores = ref([]);
        const editing = ref(false);
        const draftValues = ref();
        const draftSongCategories = ref();
        const loading = ref(true);
        const saving = ref(false);
        const error = ref(null);

        // Computed
        const isNew = computed(() => !song.id);
        const youtubeId = computed(() => song.recordingUrl ? song.recordingUrl.split("?v=")[1] : null);

        onMounted(() => {
            const categoriesLoaded = categoryStore.fetchAll();
            if (route.name === "NewSong") {
                draftValues.value = {
                    songbook: {},
                };
                draftSongCategories.value = {
                    season: [],
                    liturgical: []
                }
                editing.value = true;
                categoriesLoaded.finally(() => loading.value = false);
            } else {
                const songId = route.params.id;
                const songLoaded = songStore.get(songId)
                    .then((value) => {
                        if (!value.songbook) {
                            value.songbook = {};
                        }
                        song.value = value;
                    });
                scoreStore.fetchForSong(songId).then((value) => scores.value = value);
                const songCategoriesLoaded = categoryStore.getForSong(songId);
                Promise.all([songLoaded, categoriesLoaded, songCategoriesLoaded])
                    .then(([, , songCategoryResponse]) => {
                        songCategories.value = {};
                        songCategories.value.season = categories.value.season.filter(category => songCategoryResponse.some(songCategory => category._links.self.href === songCategory._links.self.href));
                        songCategories.value.liturgical = categories.value.liturgical.filter(category => songCategoryResponse.some(songCategory => category._links.self.href === songCategory._links.self.href));
                    })
                    .finally(() => loading.value = false);
            }
        })

        // methods
        const save = () => {
            saving.value = true;
            const songDraft = draftValues.value;
            if (!songDraft) {
                return;
            }
            const promise = saveToServer(songDraft);
            promise.then((response) => {
                if(isNew.value) {
                    songDraft.id = response.data.id;
                }
                categoryStore.putForSong(songDraft.id.toString(), draftSongCategories.value.season.concat(draftSongCategories.value.liturgical));
                editing.value = false;
                saving.value = false;
                song.value = songDraft;
                songCategories.value = draftSongCategories.value;
                draftValues.value = null;
                draftSongCategories.value = [];
                if(isNew.value) {
                    router.push({
                        name: "Song",
                        params: { id: songDraft.id },
                    });
                }
            })
        }

        const saveToServer = (song) => { // TODO
            if (song.id) {
                return api.updateSongForId(song.id, song);
            } else {
                return api.createNewSong(song);
            }
        }

        const remove = () => {
            api.deleteSongForId(song.value.id).then((response) => {
                router.push({ name: "Repertoire" });
            });
        }

        const edit = () => {
            draftValues.value = song.value;
            draftSongCategories.value = Object.assign({}, songCategories.value);
            editing.value = true;
        }

        const cancelEdit = () => {
            draftValues.value = null;
            draftSongCategories.value = null;
            editing.value = false;
        }

        const addScore = () => {
            let newScore = {
                id: null,
                song: song.value._links.self.href
            };
            scores.value.push(newScore);
        }

        const removeScore = (score) => {
            if(score.id) {
                api.deleteScoreForId(score.id);
            }
            scores.value = scores.value.filter(current => current.id !== score.id);
        }

        return { categories, song, isNew, youtubeId, songCategories, scores, editing, draftValues, draftSongCategories, loading, saving, error, save, saveToServer, remove, edit, cancelEdit, addScore, removeScore };
    },
    directives: {
        "song-focus": function (el, binding) {
            if (binding.value) {
                el.focus();
            }
        },
    },
    components: {
        Score,
        VueMultiselect,
    }
}

</script>

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
<style>
[v-cloak] {
    display: none;
}
.select .is-multiple {
    width: 100%;
}
</style>