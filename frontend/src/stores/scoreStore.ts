import { defineStore } from "pinia";
import api from "../api.js";
import { CacheMap } from "@/types/CacheMaps.js";
import { Score } from "@/types/index.js";

export const useScores = defineStore('scores', {
    state: () => ({
        scores: new CacheMap<number, Score>(),
        loading: false,
        error: null as string | null
    }),
    getters: {
        allScores(state) {
            console.log("All scores:", [...state.scores.values()]);
            return [...state.scores.values()];
        },
        scoresForSongId: (state) => (songId: number) => Array.from(state.scores.values()).filter(score => score.song.id === songId),
        count(state) {
            return state.scores.size;
        }
    },
    actions: {
        async fetch(scoreId: number) {
            this.loading = true
            const response = await api.getScoreById(scoreId)
            this.put(response.data)
            this.loading = false
            return response.data
        },

        async fetchForSong(songId: number) {
            this.loading = true;
            const response = await api.getScoresBySongId(songId)
            console.log("Scores loaded: ", response.data);
            response.data.map((score) => this.scores.set(score.id, score));
            this.loading = false;
            return response.data;
        },

        async get(scoreId: number) {
            if (!this.scores.has(scoreId)) {
                return await this.fetch(scoreId);
            } else {
                return this.scores.get(scoreId);
            }
        },

        put(score: Score) {
            this.scores.set(score.id, score);
        },

        async saveToServer(score: Score) {
            if (score.id) {
                return api.updateScoreForId(score.id, score);
            } else {
                return api.createNewScore(score);
            }
        },

        async delete(scoreId: number) {
            return api.deleteScoreForId(scoreId)
                .then((_) => {
                    this.scores.delete(scoreId);
                });
        }
    }
})