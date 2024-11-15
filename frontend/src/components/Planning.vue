<template>
    <div class="columns is-gapless">
        <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
            <aside class="menu p-2">
                <p class="menu-label">Planning</p>
                <ul class="menu-list">
                    <li>
                        <router-link :to="{ name: 'NewSetlist' }" append>
                            <span class="icon">
                                <i class="fas fa-plus-square"></i>
                            </span>
                            Add event
                        </router-link>
                    </li>
                    <li v-for="(setlist) in allSetlists" :key="setlist.id" class="droppable"
                        @drop="dropSong($event, setlist)" @dragover.prevent @dragenter.prevent>
                        <router-link :to="{ name: 'Setlist', params: { id: setlist.id } }" append>{{
                            setlist.name
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
import { useSetlists } from "@/stores/setlistStore";
import { useCategories } from "@/stores/categoryStore";
import { storeToRefs } from "pinia";
import { Setlist } from "@/types";

const setlistStore = useSetlists();

// State
const { allSetlists } = storeToRefs(setlistStore);

// Computed
const ready = computed(() => !!allSetlists);

onMounted(() => {
    setlistStore.fetchAll();
});

// Methods
const dropSong = (event: DragEvent, setlist: Setlist) => {
    let setlistUri = setlist._links?.self.href
    let songUri = event.dataTransfer?.getData("text");
    if (songUri && setlistUri)
        setlistStore.addSetlistEntry(setlistUri, songUri)
}
</script>
