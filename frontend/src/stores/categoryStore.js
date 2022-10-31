import {defineStore} from "pinia";
import api from "../api";

export const useCategories = defineStore('categories', {
    state: () => ({
        categories: {},
        categoriesBySongId: new Map(),
        loading: false,
        error: null
    }),
    getters: {},
    actions: {
        async fetchAll() {
            // if (!Object.keys(this.categories).length) {
                this.loading = true;
                api.getAllCategories()
                    .then(response => {
                        this.categories = {
                            season: response.data.filter(category => category.type === "SEASON"),
                            liturgical: response.data.filter(category => category.type === "LITURGICAL_MOMENT")
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                        this.error = "Failed to load categories";
                    });
                this.loading = false;
            // }
        },
        async fetchForSong(songId) {
            this.loading = true;
            const response = await api.getSongCategories(songId)
                .catch((error) => {
                    console.log(error);
                    this.error = "Failed to load song categories";
                });
            this.categoriesBySongId.set("hoi", "test");
            this.categoriesBySongId.set(songId, response.data);
            this.loading = false;
            return response.data;
        },
        getForSong(songId) {
            console.log(this.categoriesBySongId.keys());
            return this.categoriesBySongId.has(songId) ? this.categoriesBySongId.get(songId) : this.fetchForSong(songId)
        },
        putForSong(songId, categories) {
            let previousCategories = this.categoriesBySongId.get(songId);
            let newCategories = categories.filter(draftCategory => !previousCategories.some(previousCategory => draftCategory._links.self.href === previousCategory._links.self.href));
            let deletedCategories = previousCategories.filter(previousCategory => !categories.some(draftCategory => previousCategory._links.self.href === draftCategory._links.self.href));
            const promises = [];
            if(newCategories.length) {
                const posted = api.postSongCategories(songId, newCategories.map(songCategory => songCategory._links.self.href));
                promises.push(posted);
            }
            deletedCategories.forEach(songCategory => promises.push(api.deleteSongCategory(songId, songCategory.id)));
            Promise.all(promises)
                .then(() => {
                    if (this.categoriesBySongId.has(songId)) {
                        newCategories.map((category) => this.categoriesBySongId.get(songId).push(category));
                    } else {
                        this.categoriesBySongId.set(songId, newCategories);
                    }
                    this.categoriesBySongId.set(songId, this.categoriesBySongId.get(songId).filter((category) => deletedCategories.map((category) => category.id).includes(category.id)));
                }).catch((error) => {
                    console.log(error);
                    this.error = "Failed to load song categories";
            });
        }
    }
});