import { defineStore } from "pinia";
import { CacheMap } from "@/types/CacheMaps";
import { useSongs } from "@/application/songStore";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { Event, EventEntry } from "@/entities/event";
import { Uri } from "@/types";
import { EntityCollectionRef, EntityRef, isNew } from "@/entities/entity";

export const useEvents = defineStore('events', () => {
    const api = inject(ApiKey);
    if (!api)
        throw new Error("Api not provided");

    const eventEndpoint = api.events;
    const entriesEndpoint = api.eventEntries;
    const songStore = useSongs();

    // State
    const events = ref(new CacheMap<Uri, Event>());
    const entriesByEventUri = ref(new Map<Uri, Array<EventEntry>>());
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

    const getEntries = computed(() => (eventUri: Uri) => entriesByEventUri.value.get(eventUri) ?? []);

    // Actions
    function addTo(key: Uri, value: EventEntry): void {
        if (!entriesByEventUri.value.has(key))
            entriesByEventUri.value.set(key, []);

        entriesByEventUri.value.get(key)!.push(value)
        console.log('Added to ' + key)
    }
    
    function removeFrom(key: Uri, value: EventEntry): void {
        const values = entriesByEventUri.value.get(key);
        if (!values) return;

        const i = values.findIndex(e => e === value);
        values.splice(i, 1);
    }
    
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
        if (!data.entries.resolved) {
            fetchEntries(data);
        }

        loading.value = false;
        return data;
    }

    async function fetchEntries(event: Event) {
        if (!event.entries.uri)
             return;

        const entries = await entriesEndpoint.getAllAssociated(event.entries.uri);
        putEntries(event.uri!, entries);
    }

    async function get(eventId: number) {
        const uri =  eventEndpoint.getUri(eventId);
        if (!events.value.has(uri)) {
            return await fetch(eventId);
        } else {
            return events.value.get(uri);
        }
    }
    
    function sort(entries: EventEntry[]): EventEntry[] {
        return entries.sort((entryA, entryB) => entryA.sequence - entryB.sequence);
    } 

    function sortEntries(eventUri: Uri) {
        let entries = entriesByEventUri.value.get(eventUri);
        if (entries) {
            entriesByEventUri.value.set(eventUri, sort(entries));
        }
    }

    async function reorder(event: Event, oldIndex: number, newIndex: number): Promise<EventEntry[]> {
        const uri =  event.uri ?? eventEndpoint.getUri(event.id!)
        let entries = entriesByEventUri.value.get(uri)
        if (!entries) return [];

        const moved = entries[oldIndex];
        moved.sequence = newIndex + 1;
        
        const direction = newIndex > oldIndex ? 1 : -1;
        for (let i = newIndex; i != oldIndex; i -= direction) {
            entries[i].sequence -= direction;
        }
        entries = sort(entries);
        entriesByEventUri.value.set(uri, entries);
        entriesByEventUri.value.set(uri, entries);
        await eventEndpoint.putEntries(uri, entries);
        return entries;        
    }

    function put(event: Event) {
        const uri =  event.uri ?? eventEndpoint.getUri(event.id!)
        events.value.set(uri, event);
        if (event.entries.resolved) {
            putEntries(uri, event.entries.resolved)
        }
    }

    function putEntries(eventUri: string, entries: EventEntry[]) {
        entriesByEventUri.value.set(eventUri, entries);
        entries.forEach(entry => {
            if (entry.song?.resolved)
                songStore.put(entry.song.resolved);
        })
        sortEntries(eventUri);
    }

    async function save(event: Event) {
        if (isNew(event))
            return eventEndpoint.create(event);
        else
            return eventEndpoint.update(event);
    }

    async function addEventEntry(eventId: number, entry: { label?: string, songId?: number }) {
        const uri =  eventEndpoint.getUri(eventId);
        const event = await get(eventId);
        if (!event) {
            console.log("Event not found");
            return;
        }
        let song;
        if (entry.songId) {
            song = await songStore.get(entry.songId);
            if (!song) {
                console.log("Song not found");
                return;
            }
        }
        const sequence = (entriesByEventUri.value.get(uri) ?? []).length + 1;
        let newEntry: EventEntry = {
            event: new EntityRef(event),
            sequence,
            label: entry.label,
            song: song ? new EntityRef(song) : undefined
        }
        const response = await entriesEndpoint.create(newEntry);
        newEntry.uri = response.uri;
        addTo(uri, newEntry);
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
        removeFrom(eventUri, eventEntry);
        const entries = resequence(eventUri);
        eventEndpoint.putEntries(eventUri, entries);
    }
    
    function resequence(eventUri: Uri) {
        const entries = entriesByEventUri.value.get(eventUri)!;
        entries.forEach((entry, i) => entry.sequence = i + 1);
        return entries;
    }

    return {
        entriesByEventUri2: entriesByEventUri,
        futureEvents,
        pastEvents,
        entries: getEntries,
        get,
        fetch,
        fetchAll,
        save,
        saveEntry,
        addEventEntry,
        reorder,
        delete: remove,
        deleteEntry
    };
})