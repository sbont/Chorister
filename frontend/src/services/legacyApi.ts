import axios from "axios";
import { useAuth } from "@/services/authStore";
import { AcceptInvite, NewChoirRegistration } from "./apiTypes/registration";
import { UploadReturnEnvelope } from "@/application/fileStore";
import { Choir } from "@/entities/choir";
import { Invite } from "@/entities/invite";
import { User } from "oidc-client";
import { ApiEntityIn } from "./apiTypes";

export const SERVER_URL = import.meta.env.VITE_APP_BASE_URL + '/api';
const auth = useAuth();
const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 5000
});
instance.interceptors.request.use(
    async config => {
        let accessToken = await auth.getAccessToken()
        if (accessToken) {
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
                function (data: string) {
                    return data ? JSON.parse(data)._embedded[embeddedAttributeName] : data;
                }
            ]
        }
    },

    // Register

    register: (request: NewChoirRegistration) => instance.post('registration', request),

    // Invites

    getInviteByToken: (token: string) => instance.get<Invite>('invite?token=' + token),

    acceptInvite: (request: AcceptInvite) => instance.post('invite/accept', request),

    getToken: () => instance.get<string>('choir/invitelink'),

    deleteToken: () => instance.delete('choir/invitelink'),

    // My Choir

    getChoirs: () => instance.get<Array<Choir>>('choirs', functions.getGetConfig('choirs')),

    updateChoirForId: (id: number, choir: any) => instance.post('choirs/' + id, choir),

    // Users
    
    getUser: () => instance.get<User>('user'),

    // Files

    getUploadReturnEnvelope: () => instance.get<UploadReturnEnvelope>('/files/new-upload'),

    getUploadReturnEnvelopeForId: (id: number) => instance.get<UploadReturnEnvelope>(`/files/${id}/upload`),

    getFile: (id: number) => instance.get(`/files/${id}`),

    // Generic methods

    getAll: <Persistable extends ApiEntityIn>(path: string) => instance.get<Array<Persistable>>(path, functions.getGetConfig(path)),

    getAllRelated: <Persistable extends ApiEntityIn>(uri: string, association: string) => instance.get<Array<Persistable>>(`${uri}/${association}`, functions.getGetConfig(association)),

    getOne: <Persistable extends ApiEntityIn>(uri: string) => instance.get<Persistable>(uri),

    create: <Persistable extends ApiEntityIn>(path: string, entity: Persistable) => instance.post<Persistable>(path, entity),

    update: <Persistable extends ApiEntityIn>(uri: string, entity: Persistable) => instance.patch<Persistable>(uri, entity),

    delete: <Persistable extends ApiEntityIn>(entity: Persistable) => entity._links?.self.href ? instance.delete(entity._links?.self.href) : Promise.resolve(),

}

export default functions;