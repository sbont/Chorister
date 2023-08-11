<template>
    <div>
        <div class="is-flex is-justify-content-space-between m-3">
            <h1 class="title">{{ category.name }}</h1>
        </div>
        <Songs />
    </div>
</template>

<script>
import api from "../api.js";
import Songs from '@/components/Songs.vue'

const SongsByCategory = {
    name: "Songs by Category",
    data: function () {
        return {
            category: {
                name: "..."
            }
        };
    },
    mounted: function () {
        console.log(this.$route.params);
        var routeName = this.$route.name;
        var categoryId = this.$route.params.id;
        api.getCategoryById(categoryId)
            .then((response) => {
                this.$log.debug("Data loaded: ", response.data);
                this.category = response.data;
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load repertoire";
            });
    },
    components: {
        Songs
    }
};

export default SongsByCategory;
</script>