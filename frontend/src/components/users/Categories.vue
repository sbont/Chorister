<template>
    <div class="categories">
        <h4 class="title is-4">Categories</h4>
        <div v-if="!loading">
            <div class="columns p-3">

                <div v-for="categoryType in categoryTypes" :key="categoryType[0]" class="column is-3">
                    <h4 class="title is-5">by {{ categoryType[1].name }}</h4>
                    <CategoriesByType
                        :categories="categories(categoryType[1].uri!)"
                        :category-type="categoryType[1]" @save="onSave" @remove="onDelete" />
                </div>
            </div>
            <div class="p-3"></div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useCategories } from "@/application/categoryStore";
import CategoriesByType from "./CategoriesByType.vue";
import { AxiosError } from "axios";
import { Category } from "@/entities/category";
import { useToast } from "primevue/usetoast";
import { storeToRefs } from "pinia";
import { Uri } from "@/types";

const toast = useToast();
const categoryStore = useCategories();

// state
const loading = ref(true);

const { categoryTypes, categoriesByType } = storeToRefs(categoryStore);

categoryStore.fetchAll()
  .catch(e => toast.add({ summary: "Error saving category", detail: (e as AxiosError).message, severity: "error", closable: true }))
  .finally(() => {
    loading.value = false;
});

// Computed
const categories = computed(() => (uri: Uri) => categoriesByType.value.get(uri) ?? []);

// Methods
const onSave = async (category: Category) => {
    try {
        await categoryStore.save(category);
    } catch (e) {
        toast.add({ summary: "Error saving category", detail: (e as AxiosError).message, severity: "error", closable: true })
    }
};

const onDelete = async (category: Category) => {
    try {
        await categoryStore.deleteCategory(category);
    } catch (e) {
        toast.add({ summary: "Error saving category", detail: (e as AxiosError).message, severity: "error", closable: true })
    }
};
</script>

<style scoped>
td.grow {
    width: 99%;
}
</style>
