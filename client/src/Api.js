import axios from 'axios'

const SERVER_URL = 'http://localhost:8082/api';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

export default {

    createNew: (title, composer, songbook, songbookNumber, recordingUrl) => instance.post('songs', {
        title: title,
        composer: composer,
        recordingUrl: recordingUrl,
        addedBy: 'http://localhost:8082/api2/users/1'
    }),

    getAll: () => instance.get('songs', {
        transformResponse: [function (data) {
            return data ? JSON.parse(data)._embedded.songs : data;
        }]
    }),

    updateForId: (id, text, completed) => instance.put('todos/' + id, { title: text, completed: completed }),

    removeForId: (id) => instance.delete('todos/' + id)
}