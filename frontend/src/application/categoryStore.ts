import { defineStore } from "pinia";
import { CacheListMap } from "@/types/CacheMaps";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { Category, CategoryType } from "@/entities/category";

export const useCategories = defineStore("categories", () => {
    const api = inject(ApiKey)!;

    // state
    const categories = ref<{ season: Array<Category>, liturgical: Array<Category> }>({ season: [], liturgical: [] });
    const categoriesBySongId = ref(new CacheListMap<number, Category>());
    
    // getters
    const allCategories = computed(() => {
        return [... categories.value.season, ... categories.value.liturgical];
    });
    
    async function fetchOne(categoryId: number) {
        const data = await api.getCategoryById(categoryId);
        if (data.type === "SEASON")
            categories.value.season.push(data);
        else if(data.type === "LITURGICAL_MOMENT")
            categories.value.liturgical.push(data);
        
        return data;
    }

    async function fetchAll() {
        const data = await api.getAllCategories()      
        categories.value = {
            season: data.filter(category => category.type === "SEASON"),
            liturgical: data.filter(category => category.type === "LITURGICAL_MOMENT")
        }
    }

    async function fetchForSong(songId: number) {
        const data = await api.getSongCategories(songId);
        categoriesBySongId.value.set(songId, data);
        return data;
    }
    
    async function get(categoryId: number) {
        const found = allCategories.value.find((category) => category.id === categoryId);
        return found ?? await fetchOne(categoryId);
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
        if (category.type == CategoryType.Season) {
            categories.value.season.push(data);
        } else {
            categories.value.liturgical.push(data);
        }
    }

    async function deleteCategory(category: Category) {
        await api.deleteEntity(category);
        categories.value.liturgical = categories.value.liturgical.filter(c => c.id !== category.id);
        categories.value.season = categories.value.season.filter(c => c.id !== category.id);
    }

    return { categories, get, fetchAll, getForSong, putForSong, save, deleteCategory }
});