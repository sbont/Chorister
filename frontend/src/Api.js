import axios from 'axios'

const SERVER_URL = 'http://localhost:8082/api';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

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

}