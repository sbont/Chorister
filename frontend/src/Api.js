import axios from 'axios'
import { authService } from './auth'

const SERVER_URL = 'http://localhost:8080/api';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        let accessToken = await authService.getAccessToken()
        if(accessToken) {
            config.headers.common.Authorization = 'Bearer ' + accessToken
        }
        return config
    }
)

export default {

    // Songs

    getSongById: (id) => instance.get('songs/' + id),

    getAllSongs: () => instance.get('songs', {
        transformResponse: [function (data) {
            console.log(data);
            return data ? JSON.parse(data)._embedded.songs : data;
        }]
    }),

    createNewSong: (song) => instance.post('songs', song),

    updateSongForId: (id, song) => instance.put('songs/' + id, song),

    deleteSongForId: (id) => instance.delete('songs/' + id),

    // Scores

    getScoresBySongId: (songId) => instance.get('songs/' + songId + '/scores', {
        transformResponse: [function (data) {
            console.log(data);
            return data ? JSON.parse(data)._embedded.scores : data;
        }]
    }),

    createNewScore: (score) => instance.post('scores', score),

    updateScoreForId: (id, score) => instance.put('scores/' + id, score),

    deleteScoreForId: (id) => instance.delete('scores/' + id),

    // Register

    register: (request) => instance.post('registration', request),

}