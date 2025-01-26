<template>
    <div>
        <div class="is-flex is-justify-content-space-between m-3">
            <h1 class="title" v-if="category">{{ category.name }}</h1>
        </div>
        <Songs />
    </div>
</template>

<script setup lang="ts">
import { Category } from "@/entities/category";
import { ref } from "vue";
import Songs from "@/components/Songs.vue";
import { useRoute } from "vue-router";
import { useCategories } from "@/application/categoryStore";

// State
const category = ref<Category>();
const route = useRoute();
const categoryStore = useCategories();
const categoryId = Number(route.params.id);
categoryStore.get(categoryId).then((data) => {
    category.value = data;
});
</script>