import axios from 'axios'
import { authService } from './auth'

const SERVER_URL = 'http://localhost:8080/api';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        console.log('Starting Request', JSON.stringify(config, null, 2))
        let accessToken = await authService.getAccessToken()
        if(accessToken) {
            config.headers.common.Authorization = 'Bearer ' + accessToken
        }
        return config
    }
)

const functions = {

    getGetConfig: (embeddedAttributeName) => {
        return {
            transformResponse: [
                function(data) {
                    return data ? JSON.parse(data)._embedded[embeddedAttributeName] : data;
                }
            ]
        }
    },

    // Songs

    getSongById: (id) => instance.get('songs/' + id),

    getAllSongs: () => instance.get('songs', functions.getGetConfig('songs')),

    getSongsByCategoryId: (categoryId) => instance.get('songs/search/bycategory?id=' + categoryId, functions.getGetConfig('songs')),

    createNewSong: (song) => instance.post('songs', song),

    updateSongForId: (id, song) => instance.put('songs/' + id, song),

    deleteSongForId: (id) => instance.delete('songs/' + id),

    // Scores

    getScoresBySongId: (songId) => instance.get('songs/' + songId + '/scores', functions.getGetConfig('scores')),

    createNewScore: (score) => instance.post('scores', score),

    updateScoreForId: (id, score) => instance.put('scores/' + id, score),

    deleteScoreForId: (id) => instance.delete('scores/' + id),

    // Register

    register: (request) => instance.post('registration', request),

    // Categories

    getAllCategories: () => instance.get('categories', functions.getGetConfig('categories')),

    getSongCategories: (songId) => instance.get('songs/' + songId + '/categories', functions.getGetConfig('categories')),

    postSongCategories: (songId, categoryUris) => instance.post('songs/' + songId + '/categories', categoryUris.join('\r\n'), {
        headers: {
            'content-type': 'text/uri-list'
        }
    }),

    deleteSongCategory: (songId, categoryId) => instance.delete('songs/' + songId + '/categories/' + categoryId),

}

export default functions;