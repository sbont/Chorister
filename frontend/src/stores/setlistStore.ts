import { defineStore } from "pinia";
import api from "../services/api";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { Setlist, SetlistEntry, Song, WithEmbedded } from "@/types";
import { useSongs } from "@/stores/songStore";

export const useSetlists = defineStore('setlists', {
    state: () => ({
        setlists: new CacheMap<number, Setlist>(),
        entriesBySetlistId: new CacheListMap<number, SetlistEntry & WithEmbedded<"song", Song>>(),
        loading: false,
        error: null as string | null,
        songStore: useSongs()
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

        async fetchEntries(setlistId: number) {
            this.loading = true
            const response = await api.getSetlistEntries(setlistId)
            response.data.forEach(entry => {
                const song = entry._embedded.song;
                this.songStore.put(song)
            });
            this.entriesBySetlistId.set(setlistId, response.data)
            this.sortEntries(setlistId)
            this.loading = false
            return this.entriesBySetlistId.getOrEmpty(setlistId)
        },

        sortEntries(setlistId: number) {
            let entries = this.entriesBySetlistId.get(setlistId)
            if (entries) {
                let sorted = entries.sort((entryA, entryB) => entryA.number - entryB.number);
                this.entriesBySetlistId.set(setlistId, sorted)
            }
        },

        async move(setlistId: number, entryNumber: number, direction: number) {
            let entries = this.entriesBySetlistId.get(setlistId)
            console.log(entries);
            if (!entries) return;

            let originalIndex = entryNumber - 1
            let original = entries[originalIndex]
            let newIndex = originalIndex + direction;
            let swapped = entries[newIndex]
            console.log(originalIndex);
            console.log(original);
            console.log(newIndex);
            console.log(swapped);
            if (!swapped) return;

            original.number = newIndex + 1
            swapped.number = originalIndex + 1
            this.sortEntries(setlistId)

            let saveFirst = this.saveEntryToServer(original);
            let saveSecond = this.saveEntryToServer(swapped);
            Promise.all([saveFirst, saveSecond]).catch(error => {
                console.log(error);
                original.number = originalIndex + 1
                swapped.number = newIndex + 1
                this.sortEntries(setlistId)
            })
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

        async addSetlistEntry(setlistUri: string, songUri: string) {
            let entry = { setlist: setlistUri, song: songUri }
            await api.postSetlistEntry(entry);
        },

        async saveEntryToServer(entry: SetlistEntry) {
            if (entry.id) {
                return api.update(entry._links!.self.href, entry);
            } else {
                return api.postSetlistEntry(entry);
            }
        },

        async remove(setlistId: number) {
            return api.deleteSetlistForId(setlistId)
                .then((_) => this.setlists.delete(setlistId));
        }
    }
})