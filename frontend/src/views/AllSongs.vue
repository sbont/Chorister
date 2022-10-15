<template>
    <div>
        <div class="is-flex is-justify-content-space-between m-3">
            <h1 class="title">All Songs</h1>
            <router-link class="button is-primary" to="song/new" append tag="button">
                <span class="icon is-small">
                    <i class="fas fa-plus"></i>
                </span>
                <span>Add</span>
            </router-link>
        </div>
        <Songs />
    </div>
</template>

<script>
import Songs from '@/components/Songs.vue'

const AllSongs = {
    name: "All Songs",
    components: {
        Songs
    },
};

export default AllSongs;
</script>