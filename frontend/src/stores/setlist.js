import { defineStore } from "pinia";

export const useSetlists = defineStore('setlists', {
    state: () => {
        setlists: []
    },
    getters: {
        setlists(state) {
            return state.setlists;
        }
    },
    actions: {
        add(setlist) {
            this.setlists.push(setlist);
        }
    }
})