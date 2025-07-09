<template>
    <div class="song">
        <DetailHeader 
            :title="song?.title" 
            :subtitle="subtitle"
            :mode="!editing ? 'view' : songIsNew ? 'create' : 'edit'" 
            :onEdit="edit" 
            :edit-disabled="loading"
            :onDelete="remove" />

        <div class="song-info m-2 columns">
            <div class="column">
                <div>
                    <div class="field is-horizontal">
                        <div class="field-label is-normal">
                            <label class="label is-required">Title</label>
                        </div>
                        <div class="field-body">
                            <div class="field" v-bind:class="{ static: !editing }">
                                <div class="control">
                                    <input v-if="editing && draftValues" v-model="draftValues.title" class="input"
                                        :class="{ 'is-danger': v$.title.$error }" type="text"
                                        placeholder="The name of the song or hymn" @blur="v$.title.$touch" />
                                    <span v-else>
                                        {{ song?.title }}
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
                            <div class="field" v-bind:class="{ static: !editing }">
                                <div class="control">
                                    <input v-if="editing && draftValues" v-model="draftValues.composer" class="input"
                                        type="text" placeholder="Artist / composer / writer" />
                                    <span v-else>
                                        {{ song?.composer }}
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
                                <div class="field" v-bind:class="{ static: !editing }">
                                    <div class="control">
                                        <input v-if="editing && draftValues" v-model="draftValues.songbook!.title"
                                            class="input" type="text"
                                            placeholder="Songbook, hymnal or collection title" />
                                        <span v-else>
                                            {{ song?.songbook?.title }}
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
                                        <div class="field" v-bind:class="{ static: !editing, }">
                                            <div class="control">
                                                <input v-if="editing && draftValues"
                                                    v-model="draftValues.songbookNumber" class="input" type="text"
                                                    placeholder="Song number in the book" />
                                                <span v-else>{{ song?.songbookNumber }}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="field is-horizontal">
                        <div class="field-label is-normal">
                            <label class="label">Time of the year</label>
                        </div>
                        <div class="field-body">
                            <div class="field" v-bind:class="{ static: !editing }">
                                <div class="control">
                                    <div class="select is-multiple">
                                        <VueMultiselect v-if="editing" v-model="draftSongCategories.season"
                                            :multiple="true" :options="categories.season" track-by="name" label="name"
                                            :close-on-select="false"></VueMultiselect>

                                        <div v-else class="tags are-medium">
                                            <span v-for="(category) in songCategories?.season" :key="category.id"
                                                class="tag">
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
                            <div class="field" v-bind:class="{ static: !editing }">
                                <div class="control">
                                    <div class="select is-multiple">
                                        <VueMultiselect v-if="editing" v-model="draftSongCategories.liturgical"
                                            :multiple="true" :options="categories.liturgical" track-by="name"
                                            label="name" :close-on-select="false"></VueMultiselect>

                                        <div v-else class="tags are-medium">
                                            <span v-for="(category) in songCategories?.liturgical" :key="category.id"
                                                class="tag">
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
                    <p v-if="editing" class="control">
                        <button @click="save" class="button is-link"
                            :class="{ 'is-loading': saving, 'is-static': v$.$errors.length }">
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
                    <iframe id="ytplayer" type="text/html" :src="'https://www.youtube.com/embed/' +
                        youtubeId +
                        '?autoplay=0'
                        " frameborder="0"></iframe>
                </div>
                <div v-if="editing" class="field is-horizontal">
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }">
                            <div class="control" v-if="editing && draftValues">
                                <input v-model="draftValues.recordingUrl" class="input" type="url"
                                    placeholder="https://www.youtube.com/watch?v=..." />
                            </div>
                        </div>
                    </div>
                </div>

                <div class="text">
                    <div class="is-size-4">Text</div>
                    <div v-if="!editing" v-html="song?.text"></div>
                    <div v-else>
                        <editor-content :editor="editor" />
                    </div>
                </div>
            </div>
        </div>

        <div v-if="!editing && song">
            <ChordsArray :song="song" />
            <ScoreArray :song="song" />
        </div>

    </div>
</template>

<script setup lang="ts">
import ScoreArray from "@/components/ScoreArray.vue"
import ChordsArray from "@/components/ChordsArray.vue";
import VueMultiselect from 'vue-multiselect'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { computed, onMounted, ref, toRaw } from 'vue'
import { useVuelidate } from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import { useSongs } from "@/application/songStore";
import { useCategories } from "@/application/categoryStore";
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";
import { Song, Songbook } from "@/entities/song"
import { isNew } from "@/utils"
import { Category } from "@/entities/category";
import DetailHeader from "./ui/DetailHeader.vue";

type DraftSongbook = Partial<Songbook>

interface DraftSong extends Omit<Partial<Song>, "songbook"> {
    songbook?: DraftSongbook
}

const songStore = useSongs();
const categoryStore = useCategories();
const route = useRoute();
const router = useRouter()

// State
const { categories } = storeToRefs(categoryStore);
const song = ref<Song>()
const songCategories = ref({ season: [] as Array<Category>, liturgical: [] as Array<Category> });
const editing = ref(false);
const draftValues = ref<DraftSong>();
const draftSongCategories = ref();
const loading = ref(true);
const saving = ref(false);
const rules = {
    title: { required },
}
const v$ = useVuelidate<DraftSong>(rules, draftValues as DraftSong)
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
const songIsNew = computed(() => !song.value || isNew(song.value))
const youtubeId = computed(() => song.value?.recordingUrl ? song.value.recordingUrl.split("?v=")[1] : null);
const subtitle = computed(() => {
    let result = "";
    if (song.value?.composer)
        result += song.value.composer
    if (song.value?.composer && song.value.songbook?.title)
        result += " | ";
    if (song.value?.songbook?.title)
        result += song.value.songbook.title;
    if (song.value?.songbook?.title && song.value.songbookNumber)
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
        const songId = Number(route.params.id);
        const songLoaded = songStore.get(songId)
            .then((value) => {
                if (value) {
                    song.value = value;
                }
            });
        const songCategoriesLoaded = categoryStore.getForSong(songId);
        Promise.all([songLoaded, categoriesLoaded, songCategoriesLoaded])
            .then(([, , songCategoryResponse]) => {
                songCategories.value = {
                    season: categories.value.season!.filter(category => songCategoryResponse!.some(songCategory => category.id === songCategory.id)),
                    liturgical: categories.value.liturgical!.filter(category => songCategoryResponse!.some(songCategory => category.id === songCategory.id))
                };
            })
            .finally(() => loading.value = false);
    }
})

// methods
const save = async () => {
    saving.value = true;
    const lyricsHtml = editor.value?.getHTML();
    const songDraft = draftValues.value;
    songDraft!.text = lyricsHtml;
    const isFormCorrect = await v$.value.$validate()
    if (!songDraft || !isFormCorrect) {
        saving.value = false;
        return;
    }
    const newSong = await songStore.save(songDraft as Song);
    const wasNew = isNew(songDraft);
    if (wasNew) {
        songDraft.id = newSong.id;
    }
    categoryStore.putForSong(newSong.id, draftSongCategories.value.season.concat(draftSongCategories.value.liturgical));
    editing.value = false;
    saving.value = false;
    song.value = songDraft as Song;
    songCategories.value = draftSongCategories.value;
    draftValues.value = undefined;
    draftSongCategories.value = [];
    if (wasNew) {
        router.push({
            name: "Song",
            params: { id: songDraft.id },
        });
    }
}

const remove = () => {
    if (song.value) {
        songStore.deleteSong(song.value)
            .then((_) => {
                router.push({ name: "Repertoire" });
            });
    }
}

const edit = () => {
    draftValues.value = structuredClone(toRaw(song.value as DraftSong))
    editor.value!.commands.setContent(draftValues.value.text ?? "")
    draftValues.value.songbook ??= {}
    draftSongCategories.value = Object.assign({}, songCategories.value);
    editing.value = true;
}

const cancelEdit = () => {
    if (songIsNew.value)
        router.push({ name: "Repertoire" });

    draftValues.value = undefined;
    draftSongCategories.value = null;
    editing.value = false;
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
    padding-bottom: 56.25%;
    /* 16:9 */
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

.label.is-required::after {
    content: ' *';
    color: red;
}
</style>