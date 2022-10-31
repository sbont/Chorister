import {defineStore} from "pinia";
import api from "../api";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useSongs = defineStore('songs', {
    state: () => ({
        songs: new Map(),
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
            response.data.forEach(song => this.songs.set(song.id, song));
            this.loading = false
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
                return this.setlists.get(songId);
            }
        },
        put(song) {
            this.songs.set(song.id, song);
        },
        async saveToServer(song) {
            if (song.id) {
                return api.updateSetlistForId(song.id, song);
            } else {
                return api.createNewSetlist(song);
            }
        },
        async remove(songId) {
            return api.deleteSetlistForId(songId)
                .then((response) => {
                    this.songs.delete(songId);
                });
        }
    }
});