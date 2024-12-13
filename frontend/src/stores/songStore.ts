import { defineStore } from "pinia";
import api from "../services/api";
import { Song } from "@/types";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";

export const useSongs = defineStore('songs', {
    state: () => ({
        songs: new CacheMap<number, Song>(),
        songsByEventId: new CacheListMap<number, Song>(),
        loading: false,
        error: null
    }),
    getters: {
        allSongs(state) {
            return [...state.songs.values()];
        } 
    },
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllSongs();
            sort(response.data);
            response.data.forEach(this.put);
            this.loading = false
            return response;
        },

        async fetch(songId: number) {
            this.loading = true;
            const response = await api.getSongById(songId)
            this.put(response.data);
            this.loading = false;
            return response.data;
        },

        async get(songId: number) {
            if (!this.songs.has(songId)) {
                return await this.fetch(songId);
            } else {
                return this.songs.get(songId);
            }
        },

        put(song: Song) {
            if (song._links?.self.templated)
                song._links.self.href = song._links.self.href.replace("{?projection}", "");
            this.songs.set(song.id, song);
        },

        save(song: Song) {
            const savePromise = this.saveToServer(song);
            savePromise.then(response => this.songs.set(response.data.id, response.data));
            return savePromise;
        },

        saveToServer(song: Song) {
            if (song.id) {
                return api.updateSongForId(song.id, song);
            } else {
                return api.createNewSong(song);
            }
        },

        async delete(songId: number) {
            await api.deleteSongForId(songId);
            this.songs.delete(songId);
        }
    }
});

const sort = (data: Array<Song>) => data.sort((songA, songB) => songA.title.localeCompare(songB.title));
