<template>
    <div class="song">
        <DetailHeader
            :title="title" :subtitle="subtitle"
            :mode="state.mode === PageState.Ready ? 'view' : state.mode === PageState.Editing && state.isNew ? 'create' : 'edit'"
            :edit-disabled="state.mode === PageState.Loading" entity="song"
            :saving="state.mode == PageState.Editing && state.isSaving"
            @edit="edit"
            @delete="remove"
            @cancel-edit="cancelEdit"
            @save="save"
        />

        <div v-if="state.mode !== PageState.Loading" class="song-info m-2 columns">
            <div class="column">
                <div>
                    <div class="field is-horizontal">
                        <div class="field-label is-normal">
                            <label class="label is-required">Title</label>
                        </div>
                        <div class="field-body">
                            <div class="field" :class="{ static: state.mode !== PageState.Editing }">
                                <div class="control">
                                    <span v-if="state.mode === PageState.Ready">
                                        {{ state.song.title }}
                                    </span>

                                    <input
                                        v-else-if="state.mode === PageState.Editing"
                                        v-model="state.draft.title" class="input"
                                        :class="{ 'is-danger': v$.title.$error }" type="text"
                                        placeholder="The name of the song or hymn" @blur="v$.title.$touch" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="field is-horizontal">
                        <div class="field-label is-normal">
                            <label class="label">Composer</label>
                        </div>
                        <div class="field-body">
                            <div class="field" :class="{ static: state.mode !== PageState.Editing }">
                                <div class="control">
                                    <span v-if="state.mode === PageState.Ready">
                                        {{ state.song?.composer }}
                                    </span>

                                    <input 
                                        v-else v-model="state.draft.composer" class="input" type="text"
                                        placeholder="Artist / composer / writer" />

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
                                <div class="field" :class="{ static: state.mode !== PageState.Editing }">
                                    <div class="control">
                                        <span v-if="state.mode === PageState.Ready">
                                            {{ state.song.songbook?.title }}
                                        </span>
                                        <input
                                            v-else v-model="state.draft.songbook!.title" class="input" type="text"
                                            placeholder="Songbook, hymnal or collection title" />
                                    </div>
                                </div>
                            </div>
                            <div class="field-flex-col is-one-third ml-5">
                                <div class="field is-horizontal">
                                    <div class="field-label is-normal">
                                        <label class="label">Number</label>
                                    </div>
                                    <div class="field-body">
                                        <div class="field" :class="{ static: state.mode !== PageState.Editing, }">
                                            <div class="control">
                                                <span v-if="state.mode === PageState.Ready">
                                                    {{ state.song?.songbookNumber }}
                                                </span>
                                                <input
                                                    v-else v-model="state.draft.songbookNumber" class="input"
                                                    type="text" placeholder="Song number in the book" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-for="(categoryType) in categoryTypes.values()" :key="categoryType.id" class="field is-horizontal">
                        <div class="field-label is-normal">
                            <label class="label">{{ categoryType.name }}</label>
                        </div>
                        <div class="field-body">
                            <div class="field" :class="{ static: state.mode !== PageState.Editing }">
                                <div class="control">
                                    <div class="select is-multiple">
                                        <VueMultiselect
                                            v-if="state.mode == PageState.Editing"
                                            :model-value="state.categoriesByType.get(categoryType.uri!)!"
                                            :multiple="true"
                                            :options="categoriesByType.get(categoryType.uri!)" track-by="name"
                                            label="name" :close-on-select="false" @update:model-value="updateDraftCategories(categoryType.uri!)($event)"></VueMultiselect>

                                        <div v-else class="tags are-medium">
                                            <span
                                                v-for="(category) in songCategoriesForType(categoryType.uri!)"
                                                :key="category.id" class="tag">
                                                {{ category.name }}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="column">
                <div class="is-size-4">Play</div>
                <div v-if="youtubeId && state.mode !== PageState.Editing" class="yt-wrapper">
                    <iframe 
                        id="ytplayer" :src="'https://www.youtube.com/embed/' +
                        youtubeId +
                        '?autoplay=0'
                        " frameborder="0"></iframe>
                </div>
                <div v-if="state.mode === PageState.Editing" class="field is-horizontal">
                    <div class="field-body">
                        <div class="field">
                            <div class="control">
                                <input
                                    v-model="state.draft.recordingUrl" class="input" type="url"
                                    placeholder="https://www.youtube.com/watch?v=..." />
                            </div>
                        </div>
                    </div>
                </div>

                <div class="text">
                    <div class="is-size-4">Text</div>
                    <!-- eslint-disable-next-line vue/no-v-html -->
                    <div v-if="state.mode === PageState.Ready" v-html="state.song.text"></div>
                    <div v-else>
                        <editor-content :editor="editor" />
                    </div>
                </div>
            </div>
        </div>

        <div v-if="state.mode === PageState.Ready">
            <ScoreArray :song="state.song" :show-add="true"/>
            <ChordsArray :song="state.song" />
        </div>

    </div>
</template>

<script setup lang="ts">
import ScoreArray from "./ScoreArray.vue";
import ChordsArray from "./ChordsArray.vue";
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
import { Category } from "@/entities/category";
import DetailHeader from "../ui/DetailHeader.vue";
import { PageState, Uri } from "@/types";
import { CacheListMap } from "@/types/cache-maps";
import { useToast } from 'primevue/usetoast';
import { ApiError } from "@/types/api-error";

type SongDetailState = SongDetailLoadingState | SongDetailViewState | SongDetailEditState | SongDetailCreateState;

type SongDetailViewState = {
    mode: PageState.Ready;
    song: Song;
}

type SongDetailEditState = {
    mode: PageState.Editing;
    isNew: false;
    song: Song;
} & SongDetailWriteState;

type SongDetailLoadingState = {
    mode: PageState.Loading;
}

type SongDetailCreateState = {
    mode: PageState.Editing;
    isNew: true;
} & SongDetailWriteState;

type SongDetailWriteState = {
    isNew: boolean;
    isSaving: boolean;
    draft: DraftSong;
    categoriesByType: CacheListMap<Uri, Category>;
}

type DraftSongbook = Partial<Songbook>

interface DraftSong extends Omit<Partial<Song>, "songbook"> {
    songbook?: DraftSongbook
}

const route = useRoute();
const router = useRouter();
const songId = Number(route.params.id);
const songStore = useSongs();
const categoryStore = useCategories();
const toast = useToast();

// State
const { categoryTypes, categoriesByType } = storeToRefs(categoryStore);
const state = ref<SongDetailState>({ mode: PageState.Loading });

const songCategoriesForType = computed(() => (uri: Uri) => (categoryStore.songCategories(songId) ?? []).filter(c => c.categoryType?.uri === uri));

const rules = {
    title: { required },
}
const v$ = useVuelidate<DraftSong>(rules, computed(() => state.value.mode === PageState.Editing ? state.value.draft : {}));
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
const youtubeId = computed(() =>
    state.value.mode === PageState.Ready && state.value.song.recordingUrl
        ? state.value.song.recordingUrl.split("?v=")[1]
        : null
);
const title = computed(() => {
    if (state.value.mode !== PageState.Ready)
        return;

    return state.value.song.title;
});
const subtitle = computed(() => {
    if (state.value.mode !== PageState.Ready && !(state.value.mode === PageState.Editing && state.value.isNew === false))
        return;

    const song = state.value.song;
    let result = "";
    if (song.composer)
        result += song.composer
    if (song.composer && song.songbook?.title)
        result += " | ";
    if (song.songbook?.title)
        result += song.songbook.title;
    if (song.songbook?.title && song.songbookNumber)
        result += " " + song.songbookNumber;

    return result;
});

onMounted(() => {
    const categoriesLoaded = categoryStore.initialize();
    if (route.name === "NewSong") {
        state.value = {
            mode: PageState.Editing,
            draft: {
                songbook: {},
            },
            categoriesByType: new CacheListMap<Uri, Category>(),
            isNew: true,
            isSaving: false
        };
    } else {
        const songLoaded = songStore.get(songId);
        const songCategoriesLoaded = categoryStore.getForSong(songId);
        Promise.all([songLoaded, categoriesLoaded, songCategoriesLoaded]).then(([song, ,]) => {
            if (song) {
                state.value = {
                    mode: PageState.Ready,
                    song,
                };
            }
        });
    }
})

// methods
const save = async () => {
    if (state.value.mode !== PageState.Editing)
        return;

    const lyricsHtml = editor.value?.getHTML();
    const isFormCorrect = await v$.value.$validate()
    if (!isFormCorrect) {
        return;
    }

    state.value = {
        ...state.value,
        draft: {
            ...state.value.draft,
            text: lyricsHtml
        },
        isSaving: true
    };

    var newSong;
    try {
        newSong = await songStore.save(state.value.draft as Song);
        const allCategories = [...state.value.categoriesByType.values()].flat();
        categoryStore.putForSong(newSong.id, allCategories);
    } catch (e) {
        const error = e as ApiError;
        toast.add({ severity: "error", summary: "Error while saving song", detail: error.message });
        state.value.isSaving = false;

        return;
    }

    state.value = {
        mode: PageState.Ready,
        song: newSong
    }

    if (route.name === "NewSong") {
        router.push({
            name: "Song",
            params: { id: newSong.id },
        });
    }
}

const remove = () => {
    if (state.value.mode === PageState.Ready) {
        songStore.deleteSong(state.value.song)
            .then(() => {
                toast.add({ severity: "info", summary: "Song deleted.", life: 3000 });
                router.push({ name: "Repertoire" });
            })
            .catch(e => toast.add({ severity: "error", summary: "Error while saving song", detail: e.message }));
    }
}

const edit = () => {
    if (state.value.mode !== PageState.Ready)
        return;

    const song = state.value.song;
    const categoriesByType = new CacheListMap<Uri, Category>();
    categoryStore.songCategories(songId).forEach(c => {
        if (c.categoryType?.uri)
            categoriesByType.addTo(c.categoryType.uri, c)
    });

    state.value = {
        mode: PageState.Editing,
        draft: {
            ...toRaw(song as DraftSong),
        },
        song,
        isNew: false,
        isSaving: false,
        categoriesByType
    }
    state.value.draft.songbook ??= {};
    editor.value?.commands.setContent(song.text ?? "");
}

const cancelEdit = () => {
    if (state.value.mode !== PageState.Editing || state.value.isSaving)
        return;

    if (state.value.isNew)
        router.push({ name: "Repertoire" });
    else {
        state.value = {
            ...state.value,
            mode: PageState.Ready
        };
    }

}

const updateDraftCategories = (categoryTypeUri: Uri) => (newValues: Category[]) => {
    if (state.value.mode === PageState.Editing)
        state.value.categoriesByType.set(categoryTypeUri, newValues);
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

.text p {
    margin-top: 12px;
    min-height: 18px;
}

.label.is-required::after {
    content: ' *';
    color: red;
}
</style>