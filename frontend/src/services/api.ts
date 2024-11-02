import axios from 'axios'
import { useAuth } from "@/stores/authStore";
import { Invite, Song, Score, Category, Setlist, SetlistEntry, Choir, User, WithEmbedded, AcceptInvite, NewChoirRegistration, Chords, ApiEntity, UploadReturnEnvelope } from "@/types";

export const SERVER_URL = import.meta.env.VITE_APP_BASE_URL + '/api';
const auth = useAuth();
const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        let accessToken = await auth.getAccessToken()
        if(accessToken) {
            config.headers.Authorization = 'Bearer ' + accessToken
        }
        return config
    }
)

const functions = {

    getGetConfig: (path: string) => {
        const embeddedAttributeName = path.split("/").pop()!;
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

    // Categories

    getCategoryById: (id: number) => instance.get<Category>('categories/' + id),

    getAllCategories: () => instance.get<Array<Category>>('categories', functions.getGetConfig('categories')),
    
    createNewCategory: (category: Category) => instance.post<Category>('categories', category),

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

    // Files

    getUploadReturnEnvelope: () => instance.get<UploadReturnEnvelope>('/files/new-upload'),

    getUploadReturnEnvelopeForId: (id: number) => instance.get<UploadReturnEnvelope>(`/files/${id}/upload`),

    getFile: (id: number) => instance.get(`/files/${id}`),

    // Scores

    getUploadUrlForScore: (scoreUri: string) => instance.get(scoreUri + '/file/upload-url'),

    putFileIdForScore: (scoreUri: string, fileId: number) => instance.put(scoreUri + '/file', fileId, { headers: { "Content-Type": "application/json" }}),

    // Generic methods

    getAll: <Persistable extends ApiEntity>(path: string) => instance.get<Array<Persistable>>(path, functions.getGetConfig(path)),

    getAllRelated: <Persistable extends ApiEntity>(uri: string, association: string) => instance.get<Array<Persistable>>(`${uri}/${association}`, functions.getGetConfig(association)),

    getOne: <Persistable extends ApiEntity>(uri: string) => instance.get<Persistable>(uri),

    create: <Persistable extends ApiEntity>(path: string, entity: Persistable) => instance.post<Persistable>(path, entity),

    update: <Persistable extends ApiEntity>(uri: string, entity: Persistable) => instance.patch<Persistable>(uri, entity),

    deleteByUri: (uri: string) => instance.delete(uri),
    
    delete: <Persistable extends ApiEntity>(entity: Persistable) => entity._links?.self.href ? instance.delete(entity._links?.self.href) : Promise.resolve(),

}

export default functions;