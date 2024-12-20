<template>
    <div>
        <div class="event-detail-container">
            <EventDetail />
        </div>
        <div class="songs-container">
            <div class="p-2">
                <progress v-if="state == State.Loading" class="progress is-medium is-info" max="100"></progress>
                <DataTable :value="entries" @row-reorder="reorder">
                    <Column row-reorder class="first-col"></Column>
                    <Column field="song.title" header="Title"></Column>
                    <Column field="song.composer" header="Composer"></Column>
                    <Column field="song.songbook.title" header="Songbook"></Column>
                    <Column field="song.songbookNumber" header="no."></Column>
                    <Column body-class="delete-btn-cell">
                        <template #body="slotProps">
                            <button class="button is-danger is-inverted is-small"
                                :class="{ 'is-loading': state == State.Deleting }"
                                @click="removeEntryFromEvent(slotProps.data as EventSong)">
                                <span class="icon is-small">
                                    <i class="fas fa-times"></i>
                                </span>
                            </button>
                        </template>
                    </Column>
                </DataTable>
                <AddEventEntry :event-id="eventId" @add="addEntry"/>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import EventDetail from '@/components/EventDetail.vue';
import { EventEntry, Song, WithEmbedded } from '@/types'
import api from "@/services/api";
import { useRoute } from "vue-router";
import { useEvents } from "@/stores/eventStore";
import { computed, onMounted, ref } from "vue";
import DataTable, { DataTableRowReorderEvent } from 'primevue/datatable';
import Column from 'primevue/column';
import { useSongs } from '@/stores/songStore';
import AddEventEntry from './AddEventEntry.vue';
import { storeToRefs } from 'pinia';


type EventSong = {
    sequence: number
    song: Song
    eventEntryUri: string
}

enum State {
    Ready,
    Loading,
    Deleting,
    Dragging
}

const state = ref<State>(State.Loading)
const route = useRoute();
const eventId = Number(route.params.id);
const eventStore = useEvents();
const entries = ref<Array<EventSong>>([]);

onMounted(async () => {
    const storeEntries = await eventStore.fetchEntries(eventId);
    entries.value = storeEntries.map(toEventSong);
    console.log(entries.value);
    
    state.value = State.Ready;
});

function toEventSong(entry: EventEntry){ return <EventSong>{
    sequence: entry.sequence,
    song: entry.song,
    eventEntryUri: entry?._links?.self.href
}}

const removeEntryFromEvent = function (entry: EventSong) {
    state.value = State.Deleting
    let entryUri = entry.eventEntryUri;
    api.deleteByUri(entryUri)
        .then((response) => {
            removeSong(entry);
        })
        .catch((error) => {
            error.value = "Failed to remove entry";
        })
        .finally(() => {
            state.value = State.Ready
        });
}

const removeSong = function (song: EventSong) {
    // notice NOT using "=>" syntax
    entries.value.splice(entries.value.indexOf(song), 1);
}

const reorder = (reorder: DataTableRowReorderEvent) => {
    eventStore.reorder(eventId, reorder.dragIndex, reorder.dropIndex);
}

const addEntry = (entry: EventEntry) => entries.value.push(toEventSong(entry));

</script>

<style>
[v-cloak] {
    display: none;
}

.move-container {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.icon.is-disabled {
    color: rgba(0, 0, 0, 0.3);
}

table td.col0 {
    padding: 0.25em 0.75em;
}

.p-datatable-tbody>tr>td.delete-btn-cell {
    padding: 0.5em 0.75em;
}

.first-col {
    width: 6%;
}

.add-row {
    text-align: center;
    width: 100%;
    padding: 0.75rem 1rem;
}
</style>