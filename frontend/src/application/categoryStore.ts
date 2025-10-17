import { defineStore } from "pinia";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { computed, inject, reactive, ref } from "vue";
import { Api, ApiKey } from "./api";
import { Category } from "@/entities/category";
import { Uri } from "@/types";
import { useEntityStore } from "./entityStore";
import { CategoryType } from "@/entities/categoryType";
import { Song } from "@/entities/song";
import { notNullOrUndefined } from "@/utils";

export const useCategories = defineStore("categories", () => {
    const api = inject(ApiKey)!;
    const categoryTypeEndpoint = (api: Api) => api.categoryTypes;
    const categoryTypeStore = useEntityStore("categoryTypes", categoryTypeEndpoint)();

    // state
    const initialized = ref(false);
    const initializing = ref(false);
    const categoryTypes = ref(new CacheMap<Uri, CategoryType>);
    const categories = ref(new CacheMap<number, Category>);
    const categoriesByType = ref(new CacheListMap<Uri, Category>());
    const categoriesBySongId = ref(new Map<number, number[]>());

    // getters
    const allCategories = computed(() => categoriesByType.value.allValues());
    const songCategories = computed(() => (songId: number) => {
        const categoryIds = categoriesBySongId.value.get(songId) ?? [];
        return categoryIds.map(id => categories.value.get(id)).filter(notNullOrUndefined);
    });

    // actions
    async function initialize() {
        if (initialized.value || initializing.value)
            return;

        initializing.value = true;
        await fetchAll();
        initializing.value = false;
        initialized.value = true;
    }

    async function fetchAll() {
        const types = await categoryTypeStore.fetchAll();
        types.filter(t => t.uri).forEach(t => categoryTypes.value.set(t.uri!, t))

        const data = await api.getAllCategories();
        categoriesByType.value.clear();
        data.forEach(put);
    }

    async function fetchForSong(songId: number) {
        const data = await api.getSongCategories(songId);
        const categoryIds = data.map(c => c.id).filter(notNullOrUndefined);
        categoriesBySongId.value.set(songId, categoryIds);

        return data;
    }

    async function get(categoryId: number) {
        if (!allCategories.value)
            await fetchAll();

        return allCategories.value.find(category => category.id === categoryId);
    }

    async function getForSong(songId: number): Promise<Category[]> {
        return categoriesBySongId.value.get(songId)?.map(id => categories.value.get(id)).filter(notNullOrUndefined)
            ?? await fetchForSong(songId);
    }

    async function putForSong(songId: number, newCategories: Category[]) {
        let previousCategories = categoriesBySongId.value.get(songId) ?? [];
        let categoriesToAdd = newCategories.filter(draftCategory => !previousCategories.some(previousCategory => draftCategory.id === previousCategory));
        let categoriesToDelete = previousCategories.filter(previousCategory => !newCategories.some(draftCategory => previousCategory === draftCategory.id));
        const promises = [];
        if (categoriesToAdd.length) {
            const posted = api.postSongCategories(songId, categoriesToAdd);
            promises.push(posted);
        }
        categoriesToDelete.forEach(songCategory => {
            const category = categories.value.get(songCategory);
            if (category)
                promises.push(api.deleteSongCategory(songId, category))
        });
        await Promise.all(promises);
        categoriesBySongId.value.set(songId, newCategories.map(c => c.id));
    }

    async function addForSongs(songs: Array<Song>, category: Category) {
        const newlyAddded = songs.filter(song => {
            const previousCategories = categoriesBySongId.value.get(song.id) ?? [];
            return !previousCategories.some(c => c === category.id);
        });
        
        if (!newlyAddded.length)
            return;

        await api.postCategorySongs(category.id, newlyAddded);
        newlyAddded.forEach(song => categoriesBySongId.value.set(song.id, [...categoriesBySongId.value.get(song.id) ?? [], category.id!]));
    }

    async function save(category: Category) {
        const data = await api.createNewCategory(category);
        put(data);
    }

    function put(category: Category) {
        categories.value.set(category.id, category);
        if (!category.categoryType?.uri) {
            console.error(`Category type unknown for category with  id ${category.id}`);
        } else {
            categoriesByType.value.addTo(category.categoryType.uri, category);
        }
    }

    async function deleteCategory(category: Category) {
        await api.deleteEntity(category);
        if (category.categoryType?.uri)
            categoriesByType.value.removeFrom(category.categoryType.uri, category);
    }

    return { categories, categoriesBySongId, categoriesByType, categoryTypes, songCategories, initialize, get, fetchAll, getForSong, putForSong, addForSongs, save, deleteCategory }
});