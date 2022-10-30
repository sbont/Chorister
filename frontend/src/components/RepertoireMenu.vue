<template>
	<aside class="menu p-2">
		<p class="menu-label">Repertoire</p>
		<ul class="menu-list">
			<li>
				<router-link :to="{ name: 'Repertoire'}">All songs</router-link>
			</li>
			<li v-if="ready">
				<div class="menu-header">By time of the year</div>
				<ul>
					<li v-for="(category) in categories.season" :key="category.id">
						<router-link :to="{ name: 'CategorySeason', params: { id: category.id }}" append>{{ category.name }}</router-link>
					</li>
				</ul>
			</li>
			<li v-if="ready">
				<div class="menu-header">By liturgical place</div>
				<ul>
					<li v-for="(category) in categories.liturgical" :key="category.id">
						<router-link :to="{ name: 'CategoryLiturgical', params: { id: category.id }}" append>{{ category.name }}</router-link>
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
			<li v-for="(setlist) in allSetlists" :key="setlist.id" class="droppable" @drop="dropSong($event, setlist._links.self.href)" @dragover.prevent @dragenter.prevent>
				<router-link :to="{ name: 'Setlist', params: { id: setlist.id }}" append>{{ setlist.name }}</router-link>
			</li>
		</ul>
	</aside>
</template>

<script>
import api from "../api";
import {computed, inject, onMounted, ref} from 'vue'
import { useSetlists } from "@/stores/setlists";
import {storeToRefs} from "pinia";

export default {
    setup() {
        const logger = inject('vuejs3-logger');

        // State
        const store = useSetlists();
        const { error, allSetlists } = storeToRefs(store);
        const categories = ref(null);

        // Computed
        const ready = computed(() => !!categories.value);

        onMounted(() => {
            store.fetchAll();
            loadCategories();
        });

        // Methods
        const dropSong = (event, setlistUri) => {
            let songUri = event.dataTransfer.getData("text");
            let entry = { setlist: setlistUri, song: songUri }
            api.postSetlistEntry(entry);
        }
        const loadCategories = function() {
            api.getAllCategories()
                .then(response => {
                    logger.debug("Categories loaded: ", response.data);
                    categories.value = {
                        season: response.data.filter(category => category.type === "SEASON"),
                        liturgical: response.data.filter(category => category.type === "LITURGICAL_MOMENT")
                    }
                })
                .catch((error) => {
                    logger.debug(error);
                    error.value = "Failed to load categories";
                });
        }
        return { store, allSetlists, categories, ready, dropSong, error }
    }
}
</script>
