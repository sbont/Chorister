<template>
    <div>
        <div class="event-detail-container">
            <EventDetail />
        </div>
        <div class="songs-container">
            <div class="p-2">
                <DataTable :value="entries" @row-reorder="reorder">
                    <Column row-reorder class="first-col"></Column>
                    <Column body-class="header" header="Title">
                        <template #body="slotProps">
                            <span v-if="(slotProps.data as EventEntry).label" class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).label }}
                            </span>
                            <router-link v-else
                                :to="{ name: 'Song', params: { id: (slotProps.data as EventEntry).song?.resolved?.id } }"
                                append class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).song?.resolved?.title }}
                            </router-link>
                        </template>
                    </Column>

                    <Column field="song.resolved.composer" header="Composer"></Column>
                    <Column field="song.resolved.songbook.title" header="Songbook"></Column>
                    <Column field="song.resolved.songbookNumber" header="no."></Column>
                    <Column body-class="delete-btn-cell">
                        <template #body="slotProps">
                            <button class="button is-danger is-inverted is-small"
                                :class="{ 'is-loading': state == State.Deleting }"
                                @click="removeEntryFromEvent(slotProps.data as EventEntry)">
                                <span class="icon is-small">
                                    <i class="fas fa-times"></i>
                                </span>
                            </button>
                        </template>
                    </Column>
                </DataTable>

                <progress v-if="state == State.Loading" class="progress is-medium is-info" max="100"></progress>

                <AddEventEntry :event-id="eventId" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import EventDetail from "@/components/EventDetail.vue";
import { useRoute } from "vue-router";
import { useEvents } from "@/application/eventStore";
import { computed, ref } from "vue";
import DataTable, { DataTableRowReorderEvent } from "primevue/datatable";
import Column from "primevue/column";
import AddEventEntry from "./AddEventEntry.vue";
import { Event, EventEntry } from "@/entities/event";
import { storeToRefs } from "pinia";

enum State {
    Ready,
    Loading,
    Deleting,
}

const state = ref<State>(State.Ready);
const route = useRoute();
const eventId = Number(route.params.id);
const eventStore = useEvents();
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const entries = computed(() => event.value ? getEntries.value(event.value.uri!) : []);

eventStore.fetch(eventId).then((result) => {
    event.value = result;
});

const removeEntryFromEvent = async function (entry: EventEntry) {
    state.value = State.Deleting;
    await eventStore.deleteEntry(entry);
    state.value = State.Ready;
};

const reorder = (reorder: DataTableRowReorderEvent) => {
    if (event.value)
        eventStore.reorder(event.value, reorder.dragIndex, reorder.dropIndex)
};

</script>

<style>
[v-cloak] {
    display: none;
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
</style>