import {defineStore} from "pinia";
import api from "../api";
import { CacheMap } from "@/types/CacheMaps";
import { Setlist } from "@/types";

export const useSetlists = defineStore('setlists', {
    state: () => ({
        setlists: new CacheMap<number, Setlist>(),
        loading: false,
        error: null as string | null
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
            response.data.forEach(setlist => this.setlists.set(setlist.id as number, setlist));
            this.loading = false
        },

        async fetch(setlistId: number) {
            this.loading = true;
            const response = await api.getSetlistById(setlistId);
            this.setlists.set(response.data.id as number, response.data);
            this.loading = false;
            return response.data;
        },

        async get(setlistId: number) {
            if (!this.setlists.has(setlistId)) {
                return await this.fetch(setlistId);
            } else {
                return this.setlists.get(setlistId);
            }
        },

        put(setlist: Setlist) {
            if (setlist.id == undefined) throw new Error("Setlist has no ID")
            this.setlists.set(setlist.id, setlist);
        },

        async saveToServer(setlist: Setlist) {
            if (setlist.id) {
                return api.updateSetlistForId(setlist.id, setlist);
            } else {
                return api.createNewSetlist(setlist);
            }
        },
        async remove(setlistId: number) {
            return api.deleteSetlistForId(setlistId)
                .then((response) => {
                    this.setlists.delete(setlistId);
            });
        }
    }
})