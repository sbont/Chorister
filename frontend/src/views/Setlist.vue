<template>
	<div>
		<div class="setlist-detail-container">
			<SetlistDetail />
		</div>
		<div class="songs-container">
			<Songs />
		</div>
	</div>
</template>

<script>
import SetlistDetail from '@/components/SetlistDetail.vue';
import Songs from '@/components/Songs.vue';
import api from "../api";

// app Vue instance
const Setlist = {
	name: "Setlist",
	components: {
		SetlistDetail,
		Songs
	}
};

export default Setlist;
</script>

<style>
[v-cloak] {
	display: none;
}
</style>