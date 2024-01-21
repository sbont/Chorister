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

<script lang="ts">
import SetlistDetail from '@/components/SetlistDetail.vue';
import Songs from '@/components/Songs.vue';

</script>

<style>
[v-cloak] {
	display: none;
}
</style>