import {defineStore} from "pinia";
import api from "../api";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useSetlists = defineStore('setlists', {
    state: () => ({
        setlists: new Map(),
        loading: false,
        error: null
    }),
    getters: {
        allSetlists(state) {
            console.log("All setlists:", [...state.setlists.values()]);
            return [...state.setlists.values()];
        },
        count(state) {
            return state.setlists.size;
        }
    },
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllSetlists()
                .catch(err => {
                    console.log(err);
                    this.error = "Failed to load setlists";
                })
            console.log("setlists loaded:", response);
            response.data.forEach(setlist => this.setlists.set(setlist.id, setlist));
            this.loading = false
        },
        async fetch(setlistId) {
            this.loading = true;
            const response = await api.getSetlistById(setlistId)
                .catch((error) => {
                    console.log(error);
                    this.error = "Failed to load setlist";
                });
            console.log("Setlist loaded: ", response.data);
            this.setlists.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },
        async get(setlistId) {
            if (!this.setlists.has(setlistId)) {
                return await this.fetch(setlistId);
            } else {
                return this.setlists.get(setlistId);
            }
        },
        put(setlist) {
            this.setlists.set(setlist.id, setlist);
        },
        async saveToServer(setlist) {
            if (setlist.id) {
                return api.updateSetlistForId(setlist.id, setlist);
            } else {
                return api.createNewSetlist(setlist);
            }
        },
        async remove(setlistId) {
            return api.deleteSetlistForId(setlistId)
                .then((response) => {
                    this.setlists.delete(setlistId);
            });
        }
    }
})