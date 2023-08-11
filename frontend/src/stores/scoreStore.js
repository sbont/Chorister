import {defineStore} from "pinia";
import api from "../api.js";
import {inject} from "vue";

const logger = inject('vuejs3-logger');

export const useScores = defineStore('scores', {
    state: () => ({
        scores: new Map(),
        loading: false,
        error: null
    }),
    getters: {
        allScores(state) {
            console.log("All scores:", [...state.scores.values()]);
            return [...state.scores.values()];
        },
        scoresForSongId: (state) => (songId) => state.scores.filter((score) => score.songId === songId), // TODO
        count(state) {
            return state.scores.size;
        }
    },
    actions: {
        async fetchForSong(songId) {
            this.loading = true;
            const response = await api.getScoresBySongId(songId)
                .catch((error) => {
                    console.log(error);
                    this.error = "Failed to load scores";
                });
            console.log("Scores loaded: ", response.data);
            response.data.map((score) => this.scores.set(score.id, score));
            this.loading = false;
            return response.data;
        },
        async get(scoreId) {
            if (!this.scores.has(scoreId)) {
                return await this.fetch(scoreId);
            } else {
                return this.scores.get(scoreId);
            }
        },
        put(score) {
            this.scores.set(score.id, score);
        },
        async saveToServer(score) {
            if (score.id) {
                return api.updateScoreForId(score.id, score);
            } else {
                return api.createNewScore(score);
            }
        },
        async delete(scoreId) {
            return api.deleteScoreForId(scoreId)
                .then((response) => {
                    this.scores.delete(scoreId);
                });
        }
    }
})