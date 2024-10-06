<template>
    <div>
        <div class="is-flex is-justify-content-space-between m-3">
            <h1 class="title" v-if="category">{{ category.name }}</h1>
        </div>
        <Songs/>
    </div>
</template>

<script setup lang="ts">
import { Category } from "@/types";
import { onMounted, ref } from "vue";
import api from "./../api.js";
import Songs from '@/components/Songs.vue'
import { useRoute } from "vue-router";


// State
const category = ref<Category>()
const route = useRoute()

onMounted(() => {
    console.log(route.params);
    let categoryId = Number(route.params.id);
    api.getCategoryById(categoryId)
        .then((response) => {
            category.value = response.data;
        })
})

</script>