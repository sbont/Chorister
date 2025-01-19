import { defineStore } from "pinia";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { useSongs } from "@/application/songStore";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { Event, EventEntry } from "@/entities/event";
import { Uri } from "@/types";
import { EntityRef, isNew } from "@/entities/entity";

export const useEvents = defineStore('events', () => {
    const api = inject(ApiKey);
    if (!api)
        throw new Error("Api not provided");

    const eventEndpoint = api.events;
    const entriesEndpoint = api.eventEntries;
    const songStore = useSongs();

    // State
    const events = ref(new CacheMap<Uri, Event>());
    const entriesByEventUri = ref(new CacheListMap<Uri, EventEntry>());
    const loading = ref(false);
    const error = ref<string | null>(null);

    // Getters
    const allEvents = computed(() => [...events.value.values()]);
    const futureEvents = computed(() => {
        const today = new Date();
        return [...events.value.values()].filter(e => new Date(e.date) >= today);
    });

    const pastEvents = computed(() => {
        const today = new Date();
        return [...events.value.values()].filter(e => new Date(e.date) < today);
    });

    const entries = computed(() => (eventUri: Uri) => entriesByEventUri.value.getOrEmpty(eventUri));
    const count = computed(() => (eventUri: Uri) => entriesByEventUri.value.getOrEmpty(eventUri).length);

    // Actions
    async function fetchAll(): Promise<Array<Event>> {
        loading.value = true;
        const data = await eventEndpoint.getAll();
        data.forEach(put);
        loading.value = false;
        return data;
    }

    async function fetch(eventId: number): Promise<Event> {
        loading.value = true;
        const data = await eventEndpoint.getOne(eventId);
        put(data);
        loading.value = false;
        return data;
    }
    
    function sort(entries: EventEntry[]): EventEntry[] {
        return entries.sort((entryA, entryB) => entryA.sequence - entryB.sequence);
    } 

    function sortEntries(eventUri: Uri) {
        let entries = entriesByEventUri.value.get(eventUri)
        if (entries) {
            entriesByEventUri.value.set(eventUri, sort(entries));
        }
    }

    async function reorder(event: Event, oldIndex: number, newIndex: number) {
        const uri =  event.uri ?? eventEndpoint.getUri(event.id!)
        let entries = entriesByEventUri.value.get(uri)
        if (!entries) return;

        const moved = entries[oldIndex];
        moved.sequence = newIndex + 1;

        const direction = newIndex > oldIndex ? 1 : -1;
        for (let i = newIndex; i != oldIndex; i -= direction) {
            entries[i].sequence -= direction;
        }
        sortEntries(uri);
        const resequenced = resequence(uri);
        return eventEndpoint.putEntries(event.entries.uri, resequenced);
    }

    async function get(eventId: number) {
        const uri =  eventEndpoint.getUri(eventId);
        if (!events.value.has(uri)) {
            return await fetch(eventId);
        } else {
            return events.value.get(uri);
        }
    }

    function put(event: Event) {
        const uri =  event.uri ?? eventEndpoint.getUri(event.id!)
        events.value.set(uri, event);
        if (event.entries.resolved) {
            entriesByEventUri.value.set(uri, event.entries.resolved);
            event.entries.resolved?.forEach(entry => {
                if (entry.song.resolved)
                    songStore.put(entry.song.resolved);
            })
        }
    }

    async function save(event: Event) {
        if (isNew(event))
            return eventEndpoint.create(event);
        else
            return eventEndpoint.update(event);
    }

    async function addEventEntry(eventId: number, entry: { label?: string, songId: number }) {
        const uri =  eventEndpoint.getUri(eventId);
        const event = await get(eventId);
        if (!event) {
            console.log("Event not found");
            return;
        }
        const song = await songStore.get(entry.songId);
        if (!song) {
            console.log("Song not found");
            return;
        }
        const sequence = entriesByEventUri.value.getOrEmpty(uri).length + 1;
        let newEntry: EventEntry = { 
            event: new EntityRef(event), 
            sequence, 
            song: new EntityRef(song) 
        }
        const response = await entriesEndpoint.create(newEntry);
        newEntry.uri = response.uri;
        entriesByEventUri.value.addTo(uri, newEntry);
        return newEntry;
    }

    async function saveEntry(entry: EventEntry) {
        if (isNew(entry))
            return entriesEndpoint.create(entry);
        else
            return entriesEndpoint.update(entry);
    }

    async function remove(event: Event) {
        await eventEndpoint.delete(event);
        const uri =  event.uri ?? eventEndpoint.getUri(event.id!)
        events.value.delete(uri);
    }
    
    async function deleteEntry(eventEntry: EventEntry) {
        const eventUri =  eventEntry.event.uri ?? eventEndpoint.getUri(eventEntry.event.id!);
        const event = events.value.get(eventUri)!;
        entriesByEventUri.value.removeFrom(eventUri, eventEntry);
        const entries = resequence(eventUri);
        eventEndpoint.putEntries(event.entries.uri, entries);
    }
    
    function resequence(eventUri: Uri) {
        const entries = entriesByEventUri.value.get(eventUri)!;
        entries.forEach((entry, i) => entry.sequence = i + 1);
        return entries;
    }

    return {
        futureEvents,
        pastEvents,
        entries,
        count,
        get,
        fetchAll,
        save,
        saveEntry,
        addEventEntry,
        reorder,
        delete: remove,
        deleteEntry
    };
})