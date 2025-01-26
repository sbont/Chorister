import { defineStore } from "pinia";
import { inject, ref } from "vue";
import { ApiKey } from "@/application/api";
import { Choir } from "@/entities/choir";

export const useChoir = defineStore("choir", () => {
    const api = inject(ApiKey)!;
    
    // state
    const choir = ref<Choir>();
    const inviteToken = ref<string>();
    
    // actions
    async function getChoir() {
        if (choir.value)
            return choir.value;
        
        choir.value = await api.getChoir();
        inviteToken.value = choir.value.inviteToken;
        return choir.value;
    }
    
    async function generateToken() {
        inviteToken.value = await api.getToken();
    }

    async function deleteToken() {
        await api.deleteToken();
        inviteToken.value = undefined;
    }

    return { inviteToken, choir, getChoir, generateToken, deleteToken };
});
