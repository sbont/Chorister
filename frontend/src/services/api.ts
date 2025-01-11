import { Api as IChoristerApi, ApiEndpoint, ScoresApiEndpoint } from "@/application/api";
import { Category as DomainCategory } from "@/entities/category";
import { Choir as DomainChoir } from "@/entities/choir";
import { Chords as DomainChords } from "@/entities/chords";
import { Entity, isNew } from "@/entities/entity";
import { Event as DomainEvent, EventEntry as DomainEventEntry } from "@/entities/event";
import { Score as DomainScore } from "@/entities/score";
import { Song as DomainSong } from "@/entities/song";
import { User as DomainUser } from "@/entities/user";
import { ApiEntityIn, ApiEntityOut } from "@/services/apiTypes";
import { useAuth } from "@/services/authStore";
import axios, { AxiosInstance } from "axios";
import { Category, fromDomainCategory, toDomainCategory } from "./apiTypes/category";
import { Choir, fromDomainChoir, toDomainChoir } from "./apiTypes/choir";
import { Chords, fromDomainChords, toDomainChords } from "./apiTypes/chords";
import {
    EventEntryGet,
    EventEntryPost,
    EventGet,
    EventPost,
    fromDomainEvent,
    fromDomainEventEntry,
    toDomainEvent,
    toDomainEventEntry,
} from "./apiTypes/event";
import { UploadReturnEnvelope } from "./apiTypes/file";
import { NewChoirRegistration } from "./apiTypes/registration";
import { fromDomainScore, Score, toDomainScore } from "./apiTypes/score";
import { fromDomainSong, Song, toDomainSong } from "./apiTypes/song";
import { fromDomainUser, toDomainUser, User } from "./apiTypes/user";
import { Uri } from "@/types";

const SERVER_URL = import.meta.env.VITE_APP_BASE_URL + "/api";

export default class ChoristerApi implements IChoristerApi {
    private readonly instance: AxiosInstance;
    private choirs: ChoirsEndpoint;
    private registration: RegistrationEndpoint;
    private categories: EntityEndpoint<DomainCategory, Category, Category>
    private users: EntityEndpoint<DomainUser, User, User>
    private user: UserEndpoint;
    private files: FilesEndpoint;

    public readonly songs: SongsEndpoint;
    public readonly chords: EntityEndpoint<DomainChords, Chords, Chords>;
    public readonly scores: ScoresEndpoint;
    public readonly events: EntityEndpoint<DomainEvent, EventGet, EventPost>;
    public readonly eventEntries: EntityEndpoint<DomainEventEntry, EventEntryGet, EventEntryPost>;

    constructor() {
        this.instance = axios.create({
            baseURL: SERVER_URL,
            timeout: 5000,
        });
        const auth = useAuth();
        this.instance.interceptors.request.use(
            async config => {
                let accessToken = await auth.getAccessToken()
                if (accessToken) {
                    config.headers.Authorization = "Bearer " + accessToken
                }
                return config
            }
        )

        this.songs = new SongsEndpoint(this.instance, "songs", fromDomainSong, toDomainSong);
        this.events = new EntityEndpoint(this.instance, "events", fromDomainEvent, toDomainEvent);
        this.eventEntries = new EntityEndpoint(this.instance, "eventEntries", fromDomainEventEntry, toDomainEventEntry);
        this.choirs = new ChoirsEndpoint(this.instance, "choirs", fromDomainChoir, toDomainChoir);
        this.registration = new RegistrationEndpoint(this.instance);
        this.categories = new EntityEndpoint(this.instance, "categories", fromDomainCategory, toDomainCategory);
        this.users = new EntityEndpoint(this.instance, "users", fromDomainUser, toDomainUser);
        this.user = new UserEndpoint(this.instance);
        this.scores = new ScoresEndpoint(this.instance, "scores", fromDomainScore, toDomainScore);
        this.files = new FilesEndpoint(this.instance);
        this.chords = new EntityEndpoint(this.instance, "chords", fromDomainChords, toDomainChords);
    }

    // Generic

    update = async <DomainEntity extends Entity>(entity: DomainEntity) => {
        if (!entity.uri)
            throw new Error("Object URI unknown");

        await this.instance.patch(entity.uri, entity);
    }

    deleteEntity = async <DomainEntity extends Entity>(entity: DomainEntity) => {
        if (!entity.uri)
            throw new Error("Object URI unknown");

        await this.instance.delete(entity.uri!);
    }

    // Registration

    register = async (choirName: string, userDisplayName: string, email: string, password: string) =>
        await this.registration.register({ choirName, displayName: userDisplayName, email, password });

    // Songs

    getSongById = (id: number) => this.songs.getOne(id);

    getAllSongs = () => this.songs.getAll();

    createNewSong = (song: DomainSong) => this.songs.create(song);

    updateSong = (song: DomainSong) => this.songs.update(song);

    deleteSong = (song: DomainSong) => this.songs.delete(song);

    getSongsByCategory = (categoryId: number) => this.songs.getSongsByCategory(categoryId);

    // Categories

    getCategoryById = (id: number) => this.categories.getOne(id);

    getAllCategories = () => this.categories.getAll();

    createNewCategory = (category: DomainCategory) => this.categories.create(category);

    // Song categories

    getSongCategories = (songId: number) => this.songs.getAllRelated(songId, "categories", toDomainCategory);

    postSongCategories = (songId: number, categories: Array<DomainCategory>) => this.categories.addAllAssociations(this.songs.getUri(songId) + "/categories", categories);

    deleteSongCategory = (songId: number, category: DomainCategory) => this.categories.deleteAssociation(this.songs.getUri(songId) + "/categories", category);

    // Chords

    getChords = (songId: number) => this.songs.getAllRelated(songId, "chords", toDomainChords);

    createChords = (chords: DomainChords) => this.chords.create(chords);

    deleteChords = (chords: DomainChords) => this.chords.delete(chords);

    // Event

    getEventById = async (id: number) => this.events.getOne(id);

    getAllEvents = async () => this.events.getAll();

    createNewEvent = async (event: DomainEvent) => this.events.create(event);

    updateEvent = async (event: DomainEvent) => this.events.update(event);

    deleteEvent = async (event: DomainEvent) => this.events.delete(event);

    getEventEntries = async (event: DomainEvent) => event.id ? this.events.getAllRelated(event.id, "entries", toDomainEventEntry) : [];

    putEventEntries = async (event: DomainEvent, entries: Array<DomainEventEntry>) => {
        if (!event.id)
            throw Error("Cannot add entries to an unsaved event")

        await this.events.putAllRelated(event.id, "entries", entries, fromDomainEventEntry)
    }

    createEventEntry = async (entry: DomainEventEntry) => this.eventEntries.create(entry);

    deleteEventEntry = async (entry: DomainEventEntry) => this.eventEntries.delete(entry);

    // Choir

    getChoir = async () => {
        const choirs = await this.choirs.getAll();
        if (choirs == undefined || choirs.length != 1)
            throw Error("Choir not found for current user.")

        return choirs[0];
    }

    updateChoir = async (choir: DomainChoir) => await this.choirs.update(choir);

    getToken = async () => await this.choirs.getToken();

    deleteToken = async () => await this.choirs.deleteToken();

    // Users

    getUser = () => this.user.get();

    getAllUsers = () => this.users.getAll();

    getUserById = (userId: number) => this.users.getOne(userId);

    updateUser = (user: DomainUser) => this.users.update(user);

    // Scores

    getScores = (songId: number) => this.songs.getAllRelated(songId, "scores", toDomainScore);

    createScore = (score: DomainScore) => this.scores.create(score);

    deleteScore = (score: DomainScore) => this.scores.delete(score);

    getUploadUrlForScore = (score: DomainScore) => this.scores.getUploadUrlForScore(score);

    putFileIdForScore = (score: DomainScore, fileId: number) => this.scores.putFileIdForScore(score, fileId);

    // Files

    getUploadReturnEnvelope = () => this.files.getUploadReturnEnvelope();

    getUploadReturnEnvelopeForId = (id: number) => this.files.getUploadReturnEnvelopeForId(id);

    getFileLocation = (fileId: number) => this.files.getFileLocation(fileId);
}

class EntityEndpoint<DomainEntity extends Entity, ApiGetEntity extends ApiEntityIn, ApiPostEntity extends ApiEntityOut> implements ApiEndpoint<DomainEntity> {
    protected instance: AxiosInstance;
    protected readonly path: string;
    protected readonly fromDomain: (e: DomainEntity) => ApiPostEntity;
    protected readonly toDomain: (a: ApiGetEntity) => DomainEntity;

    constructor(instance: AxiosInstance, path: string, fromDomain: (e: DomainEntity) => ApiPostEntity, toDomain: (a: ApiGetEntity) => DomainEntity) {
        this.instance = instance;
        this.path = path;
        this.fromDomain = fromDomain;
        this.toDomain = toDomain;
    }

    getUri = (id: number) => `${SERVER_URL}/${this.path}/${id}`;

    getOne = async (id: number) => {
        const response = await this.instance.get<ApiGetEntity>(`${this.path}/${id}`);
        return this.toDomain(response.data);
    }

    getByUri = async (uri: Uri) => {
        const response = await this.instance.get<ApiGetEntity>(uri);
        return this.toDomain(response.data);
    }

    getAll = async () => {
        const response = await this.instance.get<Array<ApiGetEntity>>(this.path, this.getGetConfig());
        return response.data.map(this.toDomain);
    }

    create = async (song: DomainEntity) => {
        const data = this.fromDomain(song);
        const response = await this.instance.post<ApiGetEntity>(this.path, data);
        return this.toDomain(response.data);
    }

    update = async (obj: DomainEntity) => {
        if (isNew(obj))
            throw new Error("Object has no id");

        const data = this.fromDomain(obj);
        const response = await this.instance.patch<ApiGetEntity>(`${this.path}/` + data.id, data);
        return this.toDomain(response.data);
    }

    delete = async (obj: DomainEntity) => {
        if (isNew(obj))
            throw new Error("Song has no id");

        await this.instance.delete("songs/" + obj.id)
    }

    getAllRelated = async <RelatedDomainEntity extends Entity, RelatedApiEntity extends ApiEntityIn>(id: number, association: string, toDomain: (e: RelatedApiEntity) => RelatedDomainEntity) => {
        const response = await this.instance.get<Array<RelatedApiEntity>>(`${this.path}/${id}/${association}`, this.getGetConfig(association));
        return response.data.map(toDomain);
    }

    getAllAssociated = async (uri: string) => {
        const response = await this.instance.get<Array<ApiGetEntity>>(uri, this.getGetConfig());
        return response.data.map(this.toDomain);
    }

    putAllRelated = async <RelatedDomainEntity extends Entity, RelatedApiEntity extends ApiEntityOut>(id: number, association: string, objects: Array<RelatedDomainEntity>, fromDomain: (e: RelatedDomainEntity) => RelatedApiEntity) => {
        const data = objects.map(fromDomain);
        await this.instance.put<Array<RelatedApiEntity>>(`${this.path}/${id}/${association}`, data);
    }

    putAllAssociations = async (uri: string, objects: Array<DomainEntity>) => {
        const data = objects.map(this.fromDomain);
        await this.instance.put<Array<ApiPostEntity>>(uri, data);
    }

    addAllAssociations = async (uri: string, objects: Array<DomainEntity>) => {
        if (!objects.every(o => o.uri != undefined))
            throw new Error("Unknown URI for an object in " + JSON.stringify(objects));

        const uris = objects.map(o => o.uri);
        const data = uris.join("\r\n");
        await this.instance.post(uri, data, { headers: { "content-type": "text/uri-list" } });
    }

    deleteAssociation = async(uri: string, object: DomainEntity) => {
        await this.instance.delete(`${uri}/${object.id}`);
    }

    protected getGetConfig(embeddedAttributeName: string | undefined = undefined) {
        const prop = embeddedAttributeName ?? this.path.split("/").pop()!;
        return {
            transformResponse: [
                function (data: string) {
                    return data ? JSON.parse(data)._embedded[prop] : data;
                }
            ]
        }
    }
}

class SongsEndpoint extends EntityEndpoint<DomainSong, Song, Song> {
    constructor(instance: AxiosInstance, path: string, fromDomain: (e: DomainSong) => Song, toDomain: (a: Song) => DomainSong) {
        super(instance, path, fromDomain, toDomain);
    }

    getSongsByCategory = async (categoryId: number) => {
        const response = await this.instance.get<Array<Song>>("songs/search/bycategory?id=" + categoryId, this.getGetConfig("songs"));
        return response.data.map(toDomainSong);
    }
}

class ChoirsEndpoint extends EntityEndpoint<DomainChoir, Choir, Choir> {
    constructor(instance: AxiosInstance, path: string, fromDomain: (e: DomainChoir) => Choir, toDomain: (a: Choir) => DomainChoir) {
        super(instance, path, fromDomain, toDomain);
    }

    getToken = async () => {
        const response = await this.instance.get<string>("choir/invitelink");
        return response.data;
    }

    deleteToken = async () => {
        await this.instance.delete("choir/invitelink");
    }
}

class RegistrationEndpoint {
    private instance: AxiosInstance;
    private path = "registration";

    constructor(instance: AxiosInstance) {
        this.instance = instance;
    }

    register = async (request: NewChoirRegistration) => {
        await this.instance.post(this.path, request);
    }
}

class UserEndpoint {
    private instance: AxiosInstance;
    private path = "user";

    constructor(instance: AxiosInstance) {
        this.instance = instance;
    }

    get = async () => {
        const response = await this.instance.get<User>(this.path);
        return toDomainUser(response.data);
    }
}

class FilesEndpoint {
    private instance: AxiosInstance;
    private path = "files";

    constructor(instance: AxiosInstance) {
        this.instance = instance;
    }

    // Files

    getUploadReturnEnvelope = async () => {
        const response = await this.instance.get<UploadReturnEnvelope>(`${this.path}/files/new-upload`);
        return response.data;
    }

    getUploadReturnEnvelopeForId = async (id: number) => {
        const response = await this.instance.get<UploadReturnEnvelope>(`${this.path}/${id}/upload`);
        return response.data;
    }

    getFileLocation = async (fileId: number) => {
        const response = await this.instance.get(`${this.path}/${fileId}`);
        const location = response.headers["location"];
        return location;
    }
}

class ScoresEndpoint extends EntityEndpoint<DomainScore, Score, Score> implements ScoresApiEndpoint {
    constructor(instance: AxiosInstance, path: string, fromDomain: (e: DomainScore) => Score, toDomain: (a: Score) => DomainScore) {
        super(instance, path, fromDomain, toDomain);
    }

    getUploadUrlForScore = async (score: DomainScore) => {
        const uri = score.uri ?? this.getUri(score.id!);
        const response = await this.instance.get(`${uri}/file/upload-url`);
        return response.data;
    }

    putFileIdForScore = async (score: DomainScore, fileId: number) => {
        const uri = score.uri ?? this.getUri(score.id!);
        await this.instance.put(`${uri}/file`, fileId, { headers: { "Content-Type": "application/json" } });
    }
}