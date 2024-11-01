import { defineStore } from "pinia";
import api from "../services/api";
import { CacheMap } from "@/types/CacheMaps";
import { User } from "@/types";

export const useUsers = defineStore('users', {
    state: () => ({
        users: new CacheMap<number, User>(),
        loading: false,
        error: null as string | null
    }),
    getters: {},
    actions: {
        async fetch(userId: number) {
            this.loading = true;
            const response = await api.getUserById(userId)
            this.users.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        async fetchAll() {
            this.loading = true;
            const response = await api.getUsers()
            response.data.map((user) => this.users.set(user.id, user));
            this.loading = false;
            return response.data;
        },

        async get(userId: number) {
            if (!this.users.has(userId)) {
                return await this.fetch(userId);
            } else {
                return this.users.get(userId);
            }
        },

        async getCurrent() {
            this.loading = true;
            const response = await api.getUser()
            this.users.set(response.data.id, response.data);
            this.loading = false;
            return response.data;
        },

        save(user: User) {
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