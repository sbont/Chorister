import { defineStore } from "pinia";
import api from "../api";
import { Song } from "@/types";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";

export const useSongs = defineStore('songs', {
    state: () => ({
        songs: new CacheMap<number, Song>(),
        songsBySetlistId: new CacheListMap<number, Song>(),
        loading: false,
        error: null
    }),
    getters: {},
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllSongs()
            response.data.forEach(this.put);
            this.loading = false
            return response;
        },

        async fetch(songId: number) {
            this.loading = true;
            const response = await api.getSongById(songId)
            this.songs.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        async get(songId: number) {
            console.log("getting song with id: " + songId);
            if (!this.songs.has(songId)) {
                console.log("Fetching...");
                return await this.fetch(songId);
            } else {
                console.log("From store");
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

        delete(songId: number) {
            return api.deleteSongForId(songId)
                .then((response) => {
                    this.songs.delete(songId);
                });
        }
    }
});