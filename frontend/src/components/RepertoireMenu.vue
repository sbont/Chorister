<template>
    <aside class="menu p-2">
        <p class="menu-label">Repertoire</p>
        <ul class="menu-list">
            <li>
                <router-link :to="{ name: 'Repertoire' }">All songs</router-link>
            </li>
            <li v-if="ready">
                <div class="menu-header">By time of the year</div>
                <ul>
                    <li v-for="(category) in categories.season" :key="category.id">
                        <router-link :to="{ name: 'CategorySeason', params: { id: category.id } }" append>{{
                                category.name
                            }}
                        </router-link>
                    </li>
                </ul>
            </li>
            <li v-if="ready">
                <div class="menu-header">By liturgical place</div>
                <ul>
                    <li v-for="(category) in categories.liturgical" :key="category.id">
                        <router-link :to="{ name: 'CategoryLiturgical', params: { id: category.id } }" append>{{
                                category.name
                            }}
                        </router-link>
                    </li>
                </ul>
            </li>
        </ul>
        <p class="menu-label">Setlists</p>
        <ul class="menu-list">
            <li>
                <router-link :to="{ name: 'NewSetlist' }" append>
					<span class="icon">
						<i class="fas fa-plus-square"></i>
					</span>
                    Create setlist
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
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useSetlists } from "@/stores/setlistStore";
import { useCategories } from "@/stores/categoryStore";
import { storeToRefs } from "pinia";
import { Setlist } from "@/types";

const categoryStore = useCategories();
const setlistStore = useSetlists();

// State
const {allSetlists} = storeToRefs(setlistStore);
const {categories} = storeToRefs(categoryStore);

// Computed
const ready = computed(() => !!categories);

onMounted(() => {
    setlistStore.fetchAll();
    categoryStore.fetchAll();
});

// Methods
const dropSong = (event: DragEvent, setlist: Setlist) => {
    let setlistUri = setlist._links?.self.href
    let songUri = event.dataTransfer?.getData("text");
    if (songUri && setlistUri)
        setlistStore.addSetlistEntry(setlistUri, songUri)
}
</script>
