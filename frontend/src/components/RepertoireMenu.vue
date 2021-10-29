<template>
	<aside class="menu p-2">
		<p class="menu-label">Repertoire</p>
		<ul class="menu-list">
			<li><a class="is-active">All songs</a></li>
			<li>
				By time of the year
				<ul>
					<li v-for="(category) in categories.season" :key="category.id">
						<router-link :to="{ name: 'CategorySeason', params: { id: category.id }}" append>{{ category.name }}</router-link>
					</li>
				</ul>
			</li>
			<li>
				By liturgical place
				<ul>
					<li v-for="(category) in categories.liturgical" :key="category.id">
						<router-link :to="{ name: 'CategoryLiturgical', params: { id: category.id }}" append>{{ category.name }}</router-link>
					</li>
				</ul>
			</li>
		</ul>
		<p class="menu-label">Setlists</p>
		<ul class="menu-list">
			<li><a>Pentecost Mass</a></li>
			<li><a>16-05-2021 Sunday Afternoon Mass</a></li>
			<li><a>09-05-2021 Sunday Afternoon Mass</a></li>
			<li><a>02-05-2021 Sunday Afternoon Mass</a></li>
		</ul>
	</aside>
</template>

<script>

import api from "../api";

const RepertoireMenu = {
	name: 'RepertoireMenu',

	data: function () {
		return {
			categories: null
		};
	},

	mounted: function () {
		let categoriesLoaded = api.getAllCategories()
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
	},

	methods: {
		click: function() {

		}
	}
}

export default RepertoireMenu;

</script>
