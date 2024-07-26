import axios from 'axios'
import { useAuth } from "@/stores/authStore";
import { Invite, Song, Score, Category, Setlist, SetlistEntry, Choir, User, WithEmbedded, AcceptInvite, NewChoirRegistration, Chords, ApiEntity } from "@/types";

const SERVER_URL = import.meta.env.VITE_APP_BASE_URL + '/api';
const auth = useAuth();
const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        let accessToken = await auth.getAccessToken()
        if(accessToken) {
            config.headers.common.Authorization = 'Bearer ' + accessToken
        }
        return config
    }
)

const functions = {

    getGetConfig: (embeddedAttributeName: string) => {
        return {
            transformResponse: [
                function(data: string) {
                    return data ? JSON.parse(data)._embedded[embeddedAttributeName] : data;
                }
            ]
        }
    },

    // Register

    register: (request: NewChoirRegistration) => instance.post('registration', request),

    // Invites

    getInvites: () => instance.get<Array<Invite>>('invites', functions.getGetConfig('invites')),

    updateInviteForId: (id: number, invite: any) => instance.put('invites/' + id, invite),

    getInviteByToken: (token: string) => instance.get<Invite>('invite?token=' + token),

    acceptInvite: (request: AcceptInvite) => instance.post('invite/accept', request),

    getToken: () => instance.get<string>('choir/invitelink'),

    deleteToken: () => instance.delete('choir/invitelink'),

    // Songs

    getSongById: (id: number) => instance.get<Song>('songs/' + id),

    getAllSongs: () => instance.get<Array<Song>>('songs', functions.getGetConfig('songs')),

    getSongsByCategoryId: (categoryId: number) => instance.get<Array<Song>>('songs/search/bycategory?id=' + categoryId, functions.getGetConfig('songs')),

    createNewSong: (song: any) => instance.post<Song>('songs', song),

    updateSongForId: (id: number, song: any) => instance.patch<Song>('songs/' + id, song),

    deleteSongForId: (id: number) => instance.delete('songs/' + id),

    // Scores

    getScoreById: (scoreId: number) => instance.get<Score>('scores/' + scoreId),

    getScoresBySongId: (songId: number) => instance.get<Array<Score>>('songs/' + songId + '/scores', functions.getGetConfig('scores')),

    createNewScore: (score: any) => instance.post('scores', score),

    updateScoreForId: (id: number, score: any) => instance.put('scores/' + id, score),

    deleteScoreForId: (id: number) => instance.delete('scores/' + id),

    // Scores

    getChordsById: (scoreId: number) => instance.get<Chords>('chords/' + scoreId),

    getChordsesBySongId: (songId: number) => instance.get<Array<Chords>>('songs/' + songId + '/chords', functions.getGetConfig('chords')),

    createNewChords: (score: any) => instance.post('chords', score),

    updateChordsForId: (id: number, score: any) => instance.put('chords/' + id, score),

    deleteChordsForId: (id: number) => instance.delete('chords/' + id),

    // Categories

    getCategoryById: (id: number) => instance.get<Category>('categories/' + id),

    getAllCategories: () => instance.get<Array<Category>>('categories', functions.getGetConfig('categories')),

    // Song categories

    getSongCategories: (songId: number) => instance.get<Array<Category>>('songs/' + songId + '/categories', functions.getGetConfig('categories')),

    postSongCategories: (songId: number, categoryUris: string[]) => instance.post('songs/' + songId + '/categories', categoryUris.join('\r\n'), {
        headers: {
            'content-type': 'text/uri-list'
        }
    }),

    deleteSongCategory: (songId: number, categoryId: number) => instance.delete('songs/' + songId + '/categories/' + categoryId),

    // Setlists

    getSetlistById: (id: number) => instance.get<Setlist>('setlists/' + id),

    getAllSetlists: () => instance.get<Array<Setlist>>('setlists', functions.getGetConfig('setlists')),

    createNewSetlist: (setlist: any) => instance.post('setlists', setlist),

    updateSetlistForId: (id: number, setlist: any) => instance.put('setlists/' + id, setlist),

    deleteSetlistForId: (id: number) => instance.delete('setlists/' + id),

    // Setlist songs

    getSetlistEntries: (setlistId: number) => instance.get<Array<SetlistEntry & WithEmbedded<"song", Song>>>('setlists/' + setlistId + '/entries', functions.getGetConfig('setlistEntries')),

    postSetlistEntry: (entry: any) => instance.post("setlistEntries", entry, {
        headers: {
            'content-type': 'application/json'
        }
    }),

    deleteSetlistEntry: (setlistEntryId: string) => instance.delete("setlistEntries/" + setlistEntryId),
    
    // My Choir

    getChoirs: () => instance.get<Array<Choir>>('choirs', functions.getGetConfig('choirs')),

    updateChoirForId: (id: number, choir: any) => instance.post('choirs/' + id, choir),

    // Users

    getUsers: () => instance.get<Array<User>>('users',  functions.getGetConfig('users')),

    getUserById: (userId: number) => instance.get<User>('users/' + userId),

    getUser: () => instance.get<User>('user'),

    updateUserForId: (id: number, user: any) => instance.patch('users/' + id, user),

    // Generic methods

    getAll: <Persistable extends ApiEntity>(path: string) => instance.get<Array<Persistable>>(path, functions.getGetConfig(path)),

    getAllRelated: <Persistable extends ApiEntity>(uri: string, association: string) => instance.get<Array<Persistable>>(`${uri}/${association}`, functions.getGetConfig(association)),

    getOne: <Persistable extends ApiEntity>(uri: string) => instance.get<Persistable>(uri),

    create: <Persistable extends ApiEntity>(path: string, entity: Persistable) => instance.post<Persistable>(path, entity),

    update: <Persistable extends ApiEntity>(uri: string, entity: Persistable) => instance.patch<Persistable>(uri, entity),

    delete: <Persistable extends ApiEntity>(uri: string) => instance.delete(uri),

}

export default functions;