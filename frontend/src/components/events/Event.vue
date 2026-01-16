<template>
    <div>
        <div class="event-detail-container">
            <EventDetail />
        </div>
        <div class="songs-container">
            <div class="p-2">
                <DataTable :expanded-rows="expandedRows" :value="entries" :loading="state == State.Loading" :row-class="rowClass" @row-reorder="reorder">
                    <Column v-if="authStore.userCan('update', 'eventEntry')" row-reorder class="first-col"></Column>
                    <Column expander class="expander-col"/>
                    <Column body-class="header" header="Title">
                        <template #body="slotProps">
                            <span v-if="(slotProps.data as EventEntry).label" class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).label }}
                            </span>
                            <router-link
                                v-else
                                :to="{ name: 'Song', params: { id: (slotProps.data as EventEntry).song?.embedded?.id } }"
                                class="has-text-weight-semibold">
                                {{ (slotProps.data as EventEntry).song?.embedded?.title }}
                            </router-link>
                        </template>
                    </Column>

                    <Column field="song.embedded.composer" header="Composer"></Column>
                    <Column field="song.embedded.recordingUrl" header="Recording">
                        <template #body="slotProps">
                            <a
                                v-if="(slotProps.data as EventEntry).song?.embedded?.recordingUrl"
                                :href="(slotProps.data as EventEntry).song?.embedded?.recordingUrl" target="_blank">
                                Link
                            </a>
                        </template>
                    </Column>
                    <Column v-if="authStore.userCan('delete', 'eventEntry')" body-class="delete-btn-cell">
                        <template #body="slotProps">
                            <button
                                class="button is-danger is-inverted is-small"
                                :class="{ 'is-loading': state == State.Deleting }"
                                @click="removeEntryFromEvent(slotProps.data as EventEntry)">
                                <span class="icon is-small">
                                    <i class="fas fa-times"></i>
                                </span>
                            </button>
                        </template>
                    </Column>
                    <template #footer>
                        <AddEventEntry v-if="authStore.userCan('create', 'eventEntry')" :event-id="eventId" />
                    </template>
                    <template #expansion="slotProps">
                      <EventRowDetail :entry="slotProps.data" />
                    </template>
                </DataTable>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import EventDetail from "./EventDetail.vue";
import { useRoute } from "vue-router";
import { useEvents } from "@/application/eventStore";
import { computed, ref } from "vue";
import DataTable, { DataTableRowReorderEvent } from "primevue/datatable";
import Column from "primevue/column";
import AddEventEntry from "./AddEventEntry.vue";
import { Event, EventEntry } from "@/entities/event";
import { storeToRefs } from "pinia";
import { useAuth } from "@/application/authStore";
import EventRowDetail from "./EventRowDetail.vue";

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
const entries = computed(() => event.value?.uri ? getEntries.value(event.value.uri) : []);
const expandedRows = ref<EventEntry[]>([]);

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
    if (authStore.userCan('update', 'eventEntry') && event.value)
        eventStore.reorder(event.value, reorder.dragIndex, reorder.dropIndex)
};

function rowClass(entry: EventEntry): string {
  return entry.song ? "expandable" : "";
}

</script>

<style>
[v-cloak] {
    display: none;
}

.p-datatable .first-col {
  line-height: 10pt;
  vertical-align: middle;
}

.p-datatable .expander-col {
  line-height: 10pt;
  vertical-align: middle;
}

.p-datatable tr:not(.expandable) .p-datatable-row-toggle-button {
  display: none !important;
}

.p-datatable-tbody>tr>td.first-col, 
.p-datatable-tbody>tr>td.expander-col,
.p-datatable-tbody>tr>td.delete-btn-cell {
    padding: 0.5em 0.75em;
}

.p-datatable-footer {
    padding: 0;
}

.first-col, .expander-col {
    width: 1rem;
}

.p-datatable-tbody>tr:has(+ .p-datatable-row-expansion) td {
  border-width: 0;
}

.p-datatable-tbody tr.p-datatable-row-expansion {
  background-color: rgb(250, 250, 250);
}

</style>