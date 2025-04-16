import { Category } from "@/entities/category"
import { Choir } from "@/entities/choir"
import { Chords } from "@/entities/chords"
import { Entity } from "@/entities/entity"
import { Event, EventEntry } from "@/entities/event"
import { Invite } from "@/entities/invite"
import { Score } from "@/entities/score"
import { Song } from "@/entities/song"
import { User } from "@/entities/user"
import { Uri } from "@/types"
import { InjectionKey } from "vue"

export type EndpointIdentifier = "songs" | "chords" | "scores";

export interface Api {
    // Generic
    update: (entity: Entity) => Promise<void>
    deleteEntity: (entity: Entity) => Promise<void>

    songs: ApiEndpoint<Song>
    chords: ApiEndpoint<Chords>
    scores: ScoresApiEndpoint
    events: EventsApiEndpoint
    eventEntries: ApiEndpoint<EventEntry>

    // Registration
    register: (choirName: string, userDisplayName: string, email: string, password: string) => Promise<User>
    acceptInvite: (token: string, displayName: string, email: string, password: string) => Promise<User>

    // Songs
    getSongById: (id: number) => Promise<Song>
    getAllSongs: () => Promise<Array<Song>>
    createNewSong: (song: Song) => Promise<Song>
    updateSong: (song: Song) => Promise<Song>
    deleteSong: (song: Song) => Promise<void>

    // Categories
    getSongsByCategory: (categoryId: number) => Promise<Array<Song>>
    getCategoryById: (id: number) => Promise<Category>
    getAllCategories: () => Promise<Array<Category>>
    createNewCategory: (category: Category) => Promise<Category>

    // Song categories
    getSongCategories: (songId: number) => Promise<Array<Category>>
    postSongCategories: (songId: number, categories: Array<Category>) => Promise<void>
    deleteSongCategory: (songId: number, category: Category) => Promise<void>

    // Chords
    getChords: (songId: number) => Promise<Array<Chords>>
    createChords: (chords: Chords) => Promise<Chords>
    deleteChords: (chords: Chords) => Promise<void>

    // Events
    getEventById: (id: number) => Promise<Event>
    getAllEvents: () => Promise<Array<Event>>
    createNewEvent: (event: Event) => Promise<Event>
    updateEvent: (event: Event) => Promise<Event>
    deleteEvent: (event: Event) => Promise<void>

    // Event songs
    getEventEntries: (event: Event) => Promise<Array<EventEntry>>
    putEventEntries: (event: Event, entries: Array<EventEntry>) => Promise<void>
    createEventEntry: (entry: EventEntry) => Promise<EventEntry>
    deleteEventEntry: (eventEntry: EventEntry) => Promise<void>

    // My Choir
    getChoir: () => Promise<Choir>
    updateChoir: (choir: Choir) => Promise<Choir>
    getToken: () => Promise<string>
    deleteToken: () => Promise<void>
    getInviteByToken: (token: string) => Promise<Invite>

    // Users
    getAllUsers: () => Promise<Array<User>>
    getUserById: (userId: number) => Promise<User>
    getUser: () => Promise<User>
    updateUser: (user: User) => Promise<User>

    // Scores
    getScores: (songId: number) => Promise<Array<Score>>
    createScore: (score: Score) => Promise<Score>
    deleteScore: (score: Score) => Promise<void>
    putFileIdForScore: (score: Score, fileId: number) => Promise<void>

    // Files
    getUploadReturnEnvelope: () => Promise<{ fileId: number, uploadUrl: string }>
    getUploadReturnEnvelopeForId: (id: number) => Promise<{ fileId: number, uploadUrl: string }>
    getFileLocation: (id: number) => Promise<string>


    //     // Generic methods

    //     getAll: <Persistable extends ApiEntity>(path: string) => Promise<Entity>

    //     getAllRelated: <Persistable extends ApiEntity>(uri: string, association: string) => Promise<Entity>

    //     getOne: <Persistable extends ApiEntity>(uri: string) => Promise<Entity>

    //     create: <Persistable extends ApiEntity>(path: string, entity: Persistable) => Promise<Entity>

    //     createRelated: <Persistable>(uri: string, association: string, entity: Persistable) => Promise<Entity>
    
}

export interface ApiEndpoint<TEntity extends Entity> {
    getUri: (id: number) => Uri
    getOne: (id: number) => Promise<TEntity>
    getByUri: (uri: Uri) => Promise<TEntity>
    getAll: () => Promise<Array<TEntity>>
    create: (entity: TEntity) => Promise<TEntity>
    update: (entity: TEntity) => Promise<TEntity>
    delete: (entity: TEntity) => Promise<void>
    getAllAssociated: (uri: Uri) => Promise<Array<TEntity>>
    putAllAssociations: (uri: Uri, objects: Array<TEntity>) => Promise<void>
    addAllAssociations: (uri: Uri, objects: Array<TEntity>) => Promise<void>
    deleteAssociation: (uri: Uri, object: TEntity) => Promise<void>
}

export interface ScoresApiEndpoint extends ApiEndpoint<Score> {
    getUploadUrlForScore: (score: Score) => Promise<string>
    putFileIdForScore: (score: Score, fileId: number) => Promise<void>
}

export interface EventsApiEndpoint extends ApiEndpoint<Event> {
    putEntries: (eventUri: Uri, entries: Array<EventEntry>) => Promise<void>
}

export const ApiKey: InjectionKey<Api> = Symbol("api");