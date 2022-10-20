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
					Create setlist</router-link>
			</li>
			<li v-for="(setlist) in setlists" :key="setlist.id" class="droppable" @drop="dropSong($event, setlist._links.self.href)" @dragover.prevent @dragenter.prevent>
				<router-link :to="{ name: 'Setlist', params: { id: setlist.id }}" append>{{ setlist.name }}</router-link>
			</li>
		</ul>
	</aside>
</template>

<script>

import api from "../api";
import { useSetlists } from "@/stores/setlist";

const RepertoireMenu = {
	name: 'RepertoireMenu',

	data: function () {
		return {
			categories: null,
			setlists: null
		};
	},

	mounted: function () {
        this.loadCategories();
        this.loadSetlists();

        eventBus.＄on("refresh-setlists", () => {
            this.loadSetlists();
        });
	},

	computed: {
		ready: function() {
			return !!this.categories;
		}
	},

	methods: {
        dropSong: function(event, setlistUri) {
            let songUri = event.dataTransfer.getData("text");
            let entry = { setlist: setlistUri, song: songUri }
            api.postSetlistEntry(entry);
        },

        loadSetlists: function() {
            api.getAllSetlists()
                .then(response => {
                    this.$log.debug("Setlists loaded: ", response.data);
                    this.setlists = response.data;
                })
        },

        loadCategories: function() {
            api.getAllCategories()
                .then(response => {
                    this.$log.debug("Categories loaded: ", response.data);
                    this.categories = {
                        season: response.data.filter(category => category.type === "SEASON"),
                        liturgical: response.data.filter(category => category.type === "LITURGICAL_MOMENT")
                    }
                })
                .catch((error) => {
                    this.$log.debug(error);
                    this.error = "Failed to load categories";
                });
        }
	}
}

export default RepertoireMenu;

</script>
