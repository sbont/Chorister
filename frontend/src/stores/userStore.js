import {defineStore} from "pinia";
import api from "./../api.js";

export const useUsers = defineStore('users', {
    state: () => ({
        users: new Map(),
        loading: false,
        error: null
    }),
    getters: {},
    actions: {
        async fetch(userId) {
            this.loading = true;
            const response = await api.getUserById(userId)
                .catch((error) => {
                    console.log(error);
                    this.error = "Failed to load user";
                });
            console.log("User loaded: ", response.data);
            this.users.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        async fetchAll() {
            this.loading = true;
            const response = await api.getUsers()
                .catch((error) => {
                    console.error(error);
                    this.error = "Failed to load users";
                });
            console.log("Users loaded: ", response.data);
            response.data.map((user) => this.users.set(user.id, user));
            this.loading = false;
            return response.data;
        },

        async get(userId) {
            if (!this.users.has(userId)) {
                return await this.fetch(userId);
            } else {
                return this.songs.get(userId);
            }
        },

        async getCurrent() {
            this.loading = true;
            const response = await api.getUser()
                .catch((error) => {
                    console.error(error);
                    this.error = "Failed to load current user";
                });
            console.log("User loaded: ", response.data);
            this.users.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        save(user) {
            console.log(user);
            this.users.set(user.id, user);
            let changes = {
                email: user.email,
                username: user.username,
                displayName: user.displayName
            }
            console.log(changes);
            return api.updateUserForId(user.id, changes);
        }
    }
})