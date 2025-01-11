import { defineStore } from "pinia";
import { Song } from "@/entities/song";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { Score } from "@/entities/score";
import { Chords } from "@/entities/chords";

export const useSongs = defineStore("songs", () => {
    const api = inject(ApiKey)!;

    // state
    const songs = ref(new CacheMap<number, Song>());
    const chordsBySong = ref(new CacheListMap<number, Chords>);
    const scoresBySong = ref(new CacheListMap<number, Score>);

    // computed
    const allSongs = computed(() => [...songs.value.values()]);

    // actions
    function put(song: Song, chords?: Array<Chords>, scores?: Array<Score>) {
        songs.value.set(song.id, song);
        if (chords)
            chordsBySong.value.addAllTo(song.id, chords);
        if (scores)
            scoresBySong.value.addAllTo(song.id, scores);
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

    return { allSongs, fetchAll, get, save, deleteSong, fetchAllForCategory, put }
});

const sort = (data: Array<Song>) => data.sort((songA, songB) => songA.title.localeCompare(songB.title));
