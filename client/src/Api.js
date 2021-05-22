import axios from 'axios'

const SERVER_URL = 'http://localhost:8082/api';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

export default {

    getById: (id) => instance.get('songs/' + id),

    getAll: () => instance.get('songs', {
        transformResponse: [function (data) {
            console.log(data);
            return data ? JSON.parse(data)._embedded.songs : data;
        }]
    }),

    createNew: (song) => instance.post('songs', song),

    updateForId: (id, song) => instance.put('songs/' + id, song),

    deleteForId: (id) => instance.delete('songs/' + id)
}