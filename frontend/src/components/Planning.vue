<template>
    <div class="columns is-gapless">
        <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
            <aside class="menu p-2">
                <p class="menu-label">Planning</p>
                <ul class="menu-list">
                    <li>
                        <router-link :to="{ name: 'NewEvent' }" append>
                            <span class="icon">
                                <i class="fas fa-plus-square"></i>
                            </span>
                            Add event
                        </router-link>
                    </li>
                    <li v-for="(event) in allEvents" :key="event.id" class="droppable" @drop="dropSong($event, event)"
                        @dragover.prevent @dragenter.prevent>
                        <router-link :to="{ name: 'Event', params: { id: event.id } }" append>{{
                            event.name
                            }}
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
import { computed, onMounted } from 'vue'
import { useEvents } from "@/stores/eventStore";
import { useCategories } from "@/stores/categoryStore";
import { storeToRefs } from "pinia";
import { Event } from "@/types";

const eventStore = useEvents();

// State
const { allEvents } = storeToRefs(eventStore);

// Computed
const ready = computed(() => !!allEvents);

onMounted(() => {
    eventStore.fetchAll();
});

// Methods
const dropSong = (dragEvent: DragEvent, event: Event) => {
    let eventUri = event._links?.self.href
    let songUri = dragEvent.dataTransfer?.getData("text");
    if (songUri && eventUri)
        eventStore.addEventEntry(eventUri, songUri)
}
</script>
