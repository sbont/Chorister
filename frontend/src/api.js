import axios from 'axios'
import { useAuth } from "@/stores/authStore";

const SERVER_URL = process.env.VUE_APP_BASE_URL + '/api';
const auth = useAuth();
const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        // console.log('Starting Request', JSON.stringify(config, null, 2))
        let accessToken = await auth.getAccessToken()
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

    // Generic

    delete: (resourceUri) => instance.delete(resourceUri),

    // Register

    register: (request) => instance.post('registration', request),

    // Invites

    getInvites: () => instance.get('invites', functions.getGetConfig('invites')),

    updateInviteForId: (id, invite) => instance.put('invites/' + id, invite),

    getInviteByToken: (token) => instance.get('invite?token=' + token),

    acceptInvite: (request) => instance.post('invite/accept', request),

    getToken: () => instance.get('choir/invitelink'),

    deleteToken: () => instance.delete('choir/invitelink'),

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

    // Categories

    getCategoryById: (id) => instance.get('categories/' + id),

    getAllCategories: () => instance.get('categories', functions.getGetConfig('categories')),

    // Song categories

    getSongCategories: (songId) => instance.get('songs/' + songId + '/categories', functions.getGetConfig('categories')),

    postSongCategories: (songId, categoryUris) => instance.post('songs/' + songId + '/categories', categoryUris.join('\r\n'), {
        headers: {
            'content-type': 'text/uri-list'
        }
    }),

    deleteSongCategory: (songId, categoryId) => instance.delete('songs/' + songId + '/categories/' + categoryId),

    // Setlists

    getSetlistById: (id) => instance.get('setlists/' + id),

    getAllSetlists: () => instance.get('setlists', functions.getGetConfig('setlists')),

    createNewSetlist: (setlist) => instance.post('setlists', setlist),

    updateSetlistForId: (id, setlist) => instance.put('setlists/' + id, setlist),

    deleteSetlistForId: (id) => instance.delete('setlists/' + id),

    // Setlist songs

    getSetlistEntries: (setlistId) => instance.get('setlists/' + setlistId + '/entries', functions.getGetConfig('setlistEntries')),

    postSetlistEntry: (entry) => instance.post("setlistEntries", entry, {
        headers: {
            'content-type': 'application/json'
        }
    }),

    deleteSetlistEntry: (setlistEntryId) => instance.delete("setlistEntries/" + setlistEntryId),
    // My Choir

    getChoirs: () => instance.get('choirs', functions.getGetConfig('choirs')),

    updateChoirForId: (id, choir) => instance.post('choirs/' + id, choir),

    // Users

    getUsers: () => instance.get('users',  functions.getGetConfig('users')),

    getUserById: (userId) => instance.get('users/' + userId),

    getUser: () => instance.get('user'),

    updateUserForId: (id, user) => instance.patch('users/' + id, user),

}

export default functions;