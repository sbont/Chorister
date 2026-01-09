import { defineStore } from "pinia";
import { CacheMap } from "@/types/cache-maps";
import { User } from "@/entities/user";
import { ApiKey } from "./api";
import { inject, ref } from "vue";

export const useUsers = defineStore("users", () => {
    const api = inject(ApiKey)!;

    // state
    const users = ref(new CacheMap<number, User>());

    // actions
    // async function fetch(userId: number) {
    //     const data = await api.getUserById(userId)
    //     users.value.set(data.id, data);
    //     return data;
    // }

    async function fetchAll() {
        const data = await api.getAllUsers()
        data.map((user) => users.value.set(user.id, user));
        return data;
    }

    // async function get(userId: number) {
    //     if (!users.value.has(userId)) {
    //         return await fetch(userId);
    //     } else {
    //         return users.value.get(userId);
    //     }
    // }

    async function getCurrent() {
        const data = await api.getUser()
        users.value.set(data.id, data);
        return data;
    }

    async function save(user: User) {
        users.value.set(user.id, user);
        return api.updateUser(user);
    }

    return { users, fetchAll, getCurrent, save };
})