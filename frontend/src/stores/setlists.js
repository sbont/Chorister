import {defineStore} from "pinia";
import api from "../api";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useSetlists = defineStore('setlists', {
    state: () => ({
        setlists2: new Map(),
        loading: false,
        error: null
    }),
    getters: {
        allSetlists(state) {
            console.log("All setlists:", [...state.setlists2.values()]);
            return [...state.setlists2.values()];
        },
        count(state) {
            return state.setlists2.size;
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
            response.data.forEach(setlist => this.setlists2.set(setlist.id, setlist));
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
            this.setlists2.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },
        async get(setlistId) {
            if (!this.setlists2.has(setlistId)) {
                console.log(setlistId);
                return await this.fetch(setlistId);
            } else {
                console.log(JSON.stringify(this.setlists2));
                return this.setlists2.get(setlistId);
            }
        },
        add(setlist) {
            this.setlists2.set(setlist.id, setlist);
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
                    this.setlists2.delete(setlistId);
            });
        }
    }
})