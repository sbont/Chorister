import { defineStore } from "pinia";
import { Song } from "@/entities/song";
import { CacheListMap, CacheMap } from "@/types/cache-maps";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { Score } from "@/entities/score";
import { Chords } from "@/entities/chords";
import { useCategories } from "./categoryStore";
import { StoreState } from "./entityStore";

export const useSongs = defineStore("songs", () => {
    const api = inject(ApiKey)!;
    const categoryStore = useCategories();

    // state
    const state = ref<StoreState>(StoreState.Uninitialized);
    const songs = ref(new CacheMap<number, Song>());
    const chordsBySong = ref(new CacheListMap<number, Chords>);
    const scoresBySong = ref(new CacheListMap<number, Score>);

    // computed
    const allSongs = computed(() => Array.from(songs.value.values()).sort((a, b) => a.title == b.title ? 0 : a.title > b.title ? 1 : -1));
    const isLoading = computed(() => state.value !== StoreState.Ready);

    // actions
    function put(song: Song, chords?: Array<Chords>, scores?: Array<Score>) {
        songs.value.set(song.id, song);
        if (chords)
            chordsBySong.value.addAllTo(song.id, chords);
        if (scores)
            scoresBySong.value.addAllTo(song.id, scores);

        categoryStore.onSongLoaded(song);
    }

    async function initialize() {
        if (state.value !== StoreState.Uninitialized)
            return;

        state.value = StoreState.Initializing;

        try {
            await fetchAll();
        } catch (e) {
            state.value = StoreState.Uninitialized;
            return;
        }

        state.value = StoreState.Ready;
    }

    async function fetchAll() {
        const data = await api.getAllSongs();
        sort(data);
        data.forEach(song => put(song));
        return data;
    }

    async function fetch(songId: number) {
        const data = await api.getSongById(songId);
        const chords = await api.getChords(songId);
        const scores = await api.getScores(songId);
        put(data, chords, scores);
        return data;
    }

    async function get(songId: number) {
        if (!songs.value.has(songId)) {
            return await fetch(songId);
        } else {
            return songs.value.get(songId);
        }
    }

    async function fetchAllForCategory(categoryId: number) {
        const data = await api.getSongsByCategory(categoryId);
        sort(data);
        data.forEach(song => put(song));
        return data;
    }

    async function save(song: Song) {
        const data = await saveToServer(song);
        songs.value.set(data.id, data);
        return data;
    }

    function saveToServer(song: Song) {
        if (song.id) {
            return api.updateSong(song);
        } else {
            return api.createNewSong(song);
        }
    }

    async function deleteSong(song: Song) {
        await api.deleteSong(song);
        songs.value.delete(song.id);
    }

    return { allSongs, state, isLoading, initialize, fetchAll, get, save, deleteSong, fetchAllForCategory, put }
});

const sort = (data: Array<Song>) => data.sort((songA, songB) => songA.title.localeCompare(songB.title));
