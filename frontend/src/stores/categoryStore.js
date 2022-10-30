import {defineStore} from "pinia";
import api from "../api";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useCategories = defineStore('categories', {
    state: () => ({
        categories: {},
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
                        console.log("Categories loaded: ", response.data);
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
    }
});