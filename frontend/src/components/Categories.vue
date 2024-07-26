<template>
    <div class="categories">
        <h4 class="title is-4">Categories</h4>
        <div v-if="!loading">
            <div class="columns p-3">
                <div class="column is-3">
                    <h4 class="title is-5">by time of the year</h4>
                    <CategoriesByType :categories="categoryStore.categories.season" :category-type="CategoryType.Season" @save="onSave" @remove="onDelete" />
                </div>
                <div class="column is-3">
                    <h4 class="title is-5">by liturgical moment</h4>
                    <CategoriesByType :categories="categoryStore.categories.liturgical" :category-type="CategoryType.Liturgical" @save="onSave" @remove="onDelete" />
                </div>
            </div>
            <div class="p-3">
                


            </div>
        </div>
    </div>
</template>


<script setup lang="ts">
import { ref } from 'vue'
import { Choir, Category, CategoryType } from "@/types"
import { PropType } from "vue";
import {useCategories} from "@/stores/categoryStore";
import CategoriesByType from "@/components/CategoriesByType.vue";

const props = defineProps({
    choir: {
        type: Object as PropType<Choir>,
        required: true
    }
})

// state
const loading = ref(true)
const saving = ref(false)
const error = ref<string>()

const categoryStore = useCategories()
categoryStore.fetchAll().finally(() => {
    loading.value = false;
})

// Computed


// Methods
const onSave = (category: Category) => {
    categoryStore.saveToServer(category);
}

const onDelete = (category: Category) => {
    categoryStore.delete(category)
}


</script>

<style scoped>
td.grow {
    width: 99%;
}

</style>
