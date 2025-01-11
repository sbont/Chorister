<template>
    <div class="columns is-gapless">
        <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
            <aside class="menu p-2">
                <p class="menu-label">Create</p>
                <ul class="menu-list">
                    <li>
                        <router-link :to="{ name: 'NewEvent' }" append>
                            <span class="icon">
                                <i class="fas fa-plus-square"></i>
                            </span>
                            Add event
                        </router-link>
                    </li>
                </ul>

                <p class="menu-label">Upcoming</p>
                <ul class="menu-list">
                    <li
                        v-for="event in futureEvents"
                        :key="event.id"
                        class="droppable"
                        @drop="dropSong($event, event)"
                        @dragover.prevent
                        @dragenter.prevent
                    >
                        <router-link :to="{ name: 'Event', params: { id: event.id } }" append
                            >{{ event.name }}
                        </router-link>
                    </li>
                </ul>

                <p class="menu-label">Past</p>
                <ul class="menu-list">
                    <li
                        v-for="event in pastEvents"
                        :key="event.id"
                        class="droppable"
                        @drop="dropSong($event, event)"
                        @dragover.prevent
                        @dragenter.prevent
                    >
                        <router-link :to="{ name: 'Event', params: { id: event.id } }" append
                            >{{ event.name }}
                        </router-link>
                    </li>
                </ul>
            </aside>
        </div>
        <div class="column">
            <router-view :key="$route.fullPath"></router-view>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { Event } from "@/entities/event";

const eventStore = useEvents();
eventStore.fetchAll();

// State
const { futureEvents, pastEvents } = storeToRefs(eventStore);

// Computed
const ready = computed(() => !!futureEvents);

// Methods
const dropSong = (dragEvent: DragEvent, event: Event) => {
    let eventId = event.id;
    let songUri = dragEvent.dataTransfer?.getData("text");
    const songId = songUri ? parseInt(songUri?.substring(songUri.lastIndexOf("/"))) : undefined;
    if (songId && eventId) eventStore.addEventEntry(eventId, { songId: songId });
};
</script>
