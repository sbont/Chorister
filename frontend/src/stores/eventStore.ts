import { defineStore } from "pinia";
import api from "../services/api";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { Event, EventEntry, Song, WithEmbedded } from "@/types";
import { useSongs } from "@/stores/songStore";
import moment from "moment";

export const useEvents = defineStore('events', {
    state: () => ({
        events: new CacheMap<number, Event>(),
        entriesByEventId: new CacheListMap<number, EventEntry>(),
        loading: false,
        error: null as string | null,
        songStore: useSongs()
    }),
    getters: {
        allEvents(state) {
            return [...state.events.values()];
        },
        futureEvents(state) {
            const today = new Date();
            return [...state.events.values()].filter(e => new Date(e.date) >= today);
        },
        pastEvents(state) {
            const today = new Date();
            return [...state.events.values()].filter(e => new Date(e.date) < today);
        },
        entries(state) {
            return (eventId: number) => state.entriesByEventId.getOrEmpty(eventId);
        },
        count(state) {
            return (eventId: number) => state.entriesByEventId.getOrEmpty(eventId).length;
        }
    },
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllEvents()
            response.data.forEach(event => this.events.set(event.id as number, event));
            this.loading = false
        },

        async fetch(eventId: number) {
            this.loading = true;
            const response = await api.getEventById(eventId);
            this.events.set(response.data.id as number, response.data);
            this.loading = false;
            return response.data;
        },

        async fetchEntries(eventId: number) {
            this.loading = true
            const response = await api.getEventEntries(eventId)
            response.data.forEach(entry => {
                const song = entry._embedded?.song;
                if (song)
                    this.songStore.put(song);
            });
            this.entriesByEventId.set(eventId, response.data)
            this.sortEntries(eventId)
            this.loading = false
            return this.entriesByEventId.getOrEmpty(eventId)
        },

        sortEntries(eventId: number) {
            let entries = this.entriesByEventId.get(eventId)
            if (entries) {
                let sorted = entries.sort((entryA, entryB) => entryA.sequence - entryB.sequence);
                this.entriesByEventId.set(eventId, sorted)
            }
        },

        async reorder(eventId: number, oldIndex: number, newIndex: number) {
            let entries = this.entriesByEventId.get(eventId)
            if (!entries) return;

            const moved = entries[oldIndex];
            moved.sequence = newIndex + 1;

            const direction = newIndex > oldIndex ? 1 : -1;
            for (let i = newIndex; i != oldIndex; i -= direction) {
                entries[i].sequence -= direction;
            }
            this.sortEntries(eventId);
            return api.putEventEntries(eventId, entries);
        },

        async get(eventId: number) {
            if (!this.events.has(eventId)) {
                return await this.fetch(eventId);
            } else {
                return this.events.get(eventId);
            }
        },

        put(event: Event) {
            if (event.id == undefined) throw new Error("Event has no ID")
            this.events.set(event.id, event);
        },

        async saveToServer(event: Event) {
            if (event.id) {
                return api.updateEventForId(event.id, event);
            } else {
                return api.createNewEvent(event);
            }
        },

        async addEventEntry(eventId: number, entry: { label?: string, songId: number }) {
            const event = await this.get(eventId);
            if (!event) {
                console.log("Event not found");
                return;
            }
            const song = await this.songStore.get(entry.songId);
            if (!song) {
                console.log("Song not found");
                return;
            }
            const sequence = this.entriesByEventId.getOrEmpty(eventId!).length;
            const newEntry = await api.createEventEntry({ event: event._links?.self.href, sequence, song: song?._links?.self.href });
            this.entriesByEventId.addTo(event.id!, newEntry.data)
            return newEntry.data;
        },

        async saveEntryToServer(entry: EventEntry) {
            if (entry.id) {
                return api.update(entry._links!.self.href, entry);
            } else {
                return api.createEventEntry(entry);
            }
        },

        async remove(eventId: number) {
            return api.deleteEventForId(eventId)
                .then((_) => this.events.delete(eventId));
        }
    }
})