<template>
    <div>
        <div class="event-detail-container">
            <EventDetail />
        </div>
        <div class="songs-container">
            <div class="p-2">
                <DataTable :value="entries" @row-reorder="authStore.userCan('update', 'eventEntry') ? reorder : undefined" :loading="state == State.Loading">
                    <Column row-reorder class="first-col" v-if="authStore.userCan('update', 'eventEntry')"></Column>
                    <Column body-class="header" header="Title">
                        <template #body="slotProps">
                            <span v-if="(slotProps.data as EventEntry).label" class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).label }}
                            </span>
                            <router-link v-else
                                :to="{ name: 'Song', params: { id: (slotProps.data as EventEntry).song?.embedded?.id } }"
                                append class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).song?.embedded?.title }}
                            </router-link>
                        </template>
                    </Column>

                    <Column field="song.embedded.composer" header="Composer"></Column>
                    <Column field="song.embedded.recordingUrl" header="Recording">
                      <template #body="slotProps">
                        <a v-if="(slotProps.data as EventEntry).song?.embedded?.recordingUrl" :href="(slotProps.data as EventEntry).song?.embedded?.recordingUrl" target="_blank">
                          Link
                        </a>
                    </template>
                    </Column>
                    <Column body-class="delete-btn-cell" v-if="authStore.userCan('delete', 'eventEntry')">
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
                    <template #footer>
                        <AddEventEntry :event-id="eventId" v-if="authStore.userCan('create', 'eventEntry')" />
                    </template>
                </DataTable>                
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
import { useAuth } from "@/application/authStore";

enum State {
    Ready,
    Loading,
    Deleting,
}

const state = ref<State>(State.Loading);
const route = useRoute();
const authStore = useAuth();
const eventId = Number(route.params.id);
const eventStore = useEvents();
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const entries = computed(() => event.value ? getEntries.value(event.value.uri!) : []);

eventStore.fetch(eventId)
    .then((result) => {
        event.value = result;
    })
    .finally(() => {
        state.value = State.Ready;
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

.p-datatable-footer {
    padding: 0;
}

.first-col {
    width: 6%;
}
</style>