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
                    <p class="subtitle" v-if="subtitle">
                        {{ subtitle }}
                    </p>
                </div>
                <div v-else class="hero-body">
                    <p class="title">{{ isNew ? "Add new" : "Edit" }}</p>
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
                                            :class="{'is-danger': v$.title.$error}"
                                            type="text"
                                            placeholder="The name of the song or hymn"
                                            @blur="v$.title.$touch"
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
                    <div class="is-size-4">Play</div>
                    <div v-if="youtubeId && !editing" class="yt-wrapper">
                        <iframe
                                id="ytplayer"
                                type="text/html"
                                :src="
                            'https://www.youtube.com/embed/' +
                            youtubeId +
                            '?autoplay=0'
                        "
                                frameborder="0"
                        ></iframe>
                    </div>
                    <div v-if="editing" class="field is-horizontal">
                        <div class="field-body">
                            <div
                                class="field"
                                v-bind:class="{ static: !editing }"
                            >
                                <div class="control" v-if="editing">
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

                    <div class="text">
                        <div class="is-size-4">Text</div>
                        <div v-if="!editing" v-html="song.text" />
                        <div v-else>
                            <editor-content :editor="editor"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="scores m-2 p-3">
                <div class="is-size-4">Scores</div>
                <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
                    <Score v-for="score in scores" :key="score.id" :value="score" @remove="removeScore(score)"></Score>
                    <div class="">
                        <button class="button is-primary" @click="addScore">
                            Add
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import Score from "@/components/Score.vue"
import VueMultiselect from 'vue-multiselect'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Document from '@tiptap/extension-document'
import Paragraph from '@tiptap/extension-paragraph'
import Text from '@tiptap/extension-text'
import { computed, onMounted, ref } from 'vue' // or '@vue/composition-api' in Vue 2.x
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
        const rules = {
            title: { required },
        }
        const v$ = useVuelidate(rules, draftValues)
        const editor = useEditor({
            content: null,
            extensions: [
                StarterKit
            ],
            editorProps: {
                attributes: {
                    class: 'textarea textarea-resizable focus:outline-none',
                },
            },
            editable: true,
        })

        // Computed
        const isNew = computed(() => !song.value.id);
        const youtubeId = computed(() => song.value.recordingUrl ? song.value.recordingUrl.split("?v=")[1] : null);
        const subtitle = computed(() => {
            let result = "";
            if (song.value.composer)
                result += song.value.composer
            if (song.value.composer && song.value.songbook.title)
                result += " | ";
            if (song.value.songbook?.title)
                result += song.value.songbook.title;
            if (song.value.songbook?.title && song.value.songbookNumber)
                result += " " + song.value.songbookNumber;
            return result;
        });

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
                        const content = value.text;
                        editor.value.commands.setContent(content)
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
        const save = async () => {
            saving.value = true;
            const lyricsHtml = editor.value.getHTML();
            const songDraft = draftValues.value;
            songDraft.text = lyricsHtml;
            const isFormCorrect = await v$.value.$validate()
            if (!songDraft || !isFormCorrect) {
                saving.value = false;
                return;
            }
            const promise = songStore.save(songDraft);
            const isNewSong = isNew.value
            promise.then((response) => {
                if(isNewSong) {
                    songDraft.id = response.data.id;
                }
                categoryStore.putForSong(songDraft.id.toString(), draftSongCategories.value.season.concat(draftSongCategories.value.liturgical));
                editing.value = false;
                saving.value = false;
                song.value = songDraft;
                songCategories.value = draftSongCategories.value;
                draftValues.value = null;
                draftSongCategories.value = [];
                if(isNewSong) {
                    router.push({
                        name: "Song",
                        params: { id: songDraft.id },
                    });
                }
            })
        }

        const remove = () =>
            songStore.delete(song.value.id.toString())
                .then((response) => {
                    router.push({ name: "Repertoire" });
                });

        const edit = () => {
            draftValues.value = song.value;
            draftSongCategories.value = Object.assign({}, songCategories.value);
            editing.value = true;
        }

        const cancelEdit = () => {
            draftValues.value = null;
            draftSongCategories.value = null;
            editing.value = false;
            if (!song.value.id)
                router.push({ name: "Repertoire" });
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
                scoreStore.delete(score.id);
            }
            scores.value = scores.value.filter(current => current.id !== score.id);
        }

        return { categories, song, isNew, youtubeId, songCategories, scores, editing, draftValues, draftSongCategories, loading, saving, error, v$, subtitle, editor,
            save, remove, edit, cancelEdit, addScore, removeScore };
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
        EditorContent
    }
}

</script>

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
<style>
[v-cloak] {
    display: none;
}
.select.is-multiple {
    width: 100%;
}
.yt-wrapper {
    position: relative;
    padding-bottom: 56.25%; /* 16:9 */
    padding-top: 25px;
    height: 0;
}
.yt-wrapper iframe {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}
.textarea-resizable {
    height: initial;
}
.text p {
    min-height: 18px;
}
</style>