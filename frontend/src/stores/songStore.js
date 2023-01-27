import {defineStore} from "pinia";
import api from "../api";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useSongs = defineStore('songs', {
    state: () => ({
        songs: new Map(),
        songsBySetlistId: new Map(),
        loading: false,
        error: null
    }),
    getters: {},
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllSongs()
                .catch(err => {
                    console.log(err);
                    this.error = "Failed to load songs";
                })
            console.log("Songs loaded:", response);
            response.data.forEach(this.put);
            this.loading = false
        },

        async fetchForSetlist(setlistId) {
            this.loading = true;
            const response = await api.getSetlistEntries(setlistId)
                .catch(err => {
                    console.log(err);
                    this.error = "Failed to load setlist";
                })
            let sorted = response.data.sort((entryA, entryB) => entryA.number - entryB.number).map(entry => entry._embedded.song);
            this.putSetlist(sorted, setlistId);
            this.loading = false;
            console.log(sorted)
            return sorted;
        },

        async fetch(songId) {
            this.loading = true;
            const response = await api.getSongById(songId)
                .catch((error) => {
                    console.log(error);
                    this.error = "Failed to load song";
                });
            console.log("Song loaded: ", response.data);
            this.songs.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        async get(songId) {
            if (!this.songs.has(songId)) {
                return await this.fetch(songId);
            } else {
                return this.songs.get(songId);
            }
        },

        async getForSetlist(setlistId) {
            if (!this.songsBySetlistId.has(setlistId)) {
                return await this.fetchForSetlist(setlistId);
            } else {
                return this.songsBySetlistId.get(setlistId);
            }
        },

        put(song) {
            this.songs.set(song.id, song);
        },

        putSetlist(songs, setlistId) {
            songs.forEach(this.put)
            this.songsBySetlistId.set(setlistId, songs)
        },

        save(song) {
            const savePromise = this.saveToServer(song);
            savePromise.then(savedSong => this.songs.set(savedSong.id, savedSong));
            return savePromise;
        },

        saveToServer(song) {
            if (song.id) {
                return api.updateSongForId(song.id, song);
            } else {
                return api.createNewSong(song);
            }
        },

        delete(songId) {
            return api.deleteSongForId(songId)
                .then((response) => {
                    this.songs.delete(songId);
                });
        }
    }
});