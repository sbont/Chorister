import { defineStore } from "pinia";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { computed, inject, ref } from "vue";
import { Api, ApiKey } from "./api";
import { Category } from "@/entities/category";
import { Uri } from "@/types";
import { useEntityStore } from "./entityStore";
import { CategoryType } from "@/entities/categoryType";

export const useCategories = defineStore("categories", () => {
    const api = inject(ApiKey)!;
    const categoryTypeEndpoint = (api: Api) => api.categoryTypes;
    const categoryTypeStore = useEntityStore("categoryTypes", categoryTypeEndpoint)();

    // state
    const categoryTypes = ref(new CacheMap<Uri, CategoryType>);
    const categories = ref(new CacheListMap<Uri, Category>());
    const categoriesBySongId = ref(new CacheListMap<number, Category>());

    // getters
    const allCategories = computed(() => categories.value.allValues());

    async function fetchAll() {
        const types = await categoryTypeStore.fetchAll();
        types.filter(t => t.uri).forEach(t => categoryTypes.value.set(t.uri!, t))

        const data = await api.getAllCategories();
        categories.value.clear();
        data.forEach(put);
    }

    async function fetchForSong(songId: number) {
        const data = await api.getSongCategories(songId);
        categoriesBySongId.value.set(songId, data);
        return data;
    }
    
    async function get(categoryId: number) {
        if (!allCategories.value)
            await fetchAll();

        return allCategories.value.find(category => category.id === categoryId);
    }

    async function getForSong(songId: number) {
        return categoriesBySongId.value.has(songId) ? categoriesBySongId.value.get(songId) : await fetchForSong(songId)
    }

    async function putForSong(songId: number, categories: Category[]) {
        let previousCategories = categoriesBySongId.value.get(songId) ?? [];
        let newCategories = categories.filter(draftCategory => !previousCategories.some(previousCategory => draftCategory.id === previousCategory.id));
        let deletedCategories = previousCategories.filter(previousCategory => !categories.some(draftCategory => previousCategory.id === draftCategory.id));
        const promises = [];
        if (newCategories.length) {
            const posted = api.postSongCategories(songId, newCategories);
            promises.push(posted);
        }
        deletedCategories.forEach(songCategory => promises.push(api.deleteSongCategory(songId, songCategory)));
        await Promise.all(promises)
        categoriesBySongId.value.addAllTo(songId, newCategories)
        categoriesBySongId.value.removeAllFrom(songId, deletedCategories)
    }

    async function save(category: Category) {
        const data = await api.createNewCategory(category);
        put(data);
    }

    function put(category: Category) {
        if (!category.categoryType?.uri) {
            console.error(`Category type unknown for category with  id ${category.id}`);
        } else {
            categories.value.addTo(category.categoryType.uri, category);
        }   
    }

    async function deleteCategory(category: Category) {
        await api.deleteEntity(category);
        if  (category.categoryType?.uri)
            categories.value.removeFrom(category.categoryType.uri, category);
    }

    return { categories, categoryTypes, get, fetchAll, getForSong, putForSong, save, deleteCategory }
});