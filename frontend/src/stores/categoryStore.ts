import { defineStore } from "pinia";
import api from "../services/api";
import { Categories, Category, CategoryType } from '@/types'
import { CacheListMap } from "@/types/CacheMaps";

export type CategoriesState = {
    categories: Categories
    categoriesBySongId: CacheListMap<number, Category>
    loading: boolean
    error: string | undefined
}

export const useCategories = defineStore('categories', {
    state: () => ({
        categories: {},
        categoriesBySongId: new CacheListMap<number, Category>(),
        loading: false
    } as CategoriesState),
    getters: {},
    actions: {
        async fetchAll() {
            this.loading = true;
            const response = await api.getAllCategories()
            this.categories = {
                season: response.data.filter(category => category.type === "SEASON"),
                liturgical: response.data.filter(category => category.type === "LITURGICAL_MOMENT")
            }
            this.loading = false;
        },

        async fetchForSong(songId: number) {
            this.loading = true;
            const response = await api.getSongCategories(songId);
            this.categoriesBySongId.set(songId, response.data);
            this.loading = false;
            return response.data;
        },

        async getForSong(songId: number) {
            return this.categoriesBySongId.has(songId) ? this.categoriesBySongId.get(songId) : await this.fetchForSong(songId)
        },

        async putForSong(songId: number, categories: Category[]) {
            let previousCategories = this.categoriesBySongId.get(songId) ?? [];
            let newCategories = categories.filter(draftCategory => !previousCategories.some(previousCategory => draftCategory.id === previousCategory.id));
            let deletedCategories = previousCategories.filter(previousCategory => !categories.some(draftCategory => previousCategory.id === draftCategory.id));
            const promises = [];
            if (newCategories.length) {
                const posted = api.postSongCategories(songId, newCategories.map(songCategory => songCategory._links!.self.href));
                promises.push(posted);
            }
            deletedCategories.forEach(songCategory => promises.push(api.deleteSongCategory(songId, songCategory.id)));
            await Promise.all(promises)
            this.categoriesBySongId.addAllTo(songId, newCategories)
            this.categoriesBySongId.removeAllFrom(songId, deletedCategories)
        },

        async saveToServer(category: Category) {
            const response = await api.createNewCategory(category);
            if (category.type == CategoryType.Season) {
                this.categories.season.push(response.data);
            } else {
                this.categories.liturgical.push(response.data);
            }
        },

        async delete(category: Category) {
            await api.delete(category);
            this.categories.liturgical = this.categories.liturgical.filter(c => c.id !== category.id);
            this.categories.season = this.categories.season.filter(c => c.id !== category.id);
        }
    }
});