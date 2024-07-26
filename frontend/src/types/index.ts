import { Key } from "./Key"

export interface Identifiable extends ApiEntity {
    id: number | undefined
}

export interface ApiEntity {
    _links: { self: { href: string }} | undefined
}

export interface Choir extends Identifiable {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager: User
}

export interface User extends Identifiable {
    id: number,
    choir: Choir,
    email: string,
    username: string,
    displayName: string
}

export interface Invite extends Identifiable {
    id: number
    email: string
    invitedBy: User
    token: string
    createdDate: Date
    acceptedDate: Date
    expired: Boolean
    choir: Choir
}

export interface Registration {
    displayName: string
    email: string
    password: string
}

export interface NewChoirRegistration extends Registration {
    choirName: string
}

export interface AcceptInvite extends Registration {
    token: string
}


export interface Categories {
    season?: Array<Category> | undefined,
    liturgical?: Array<Category> | undefined
}

export interface Category extends Identifiable {
    id: number,
    choir: Choir,
    name: string,
    type: CategoryType
}

enum CategoryType {
    Season = "SEASON",
    Liturgical = "LITURGICAL_MOMENT"
}

export interface Score extends ApiEntity {
    song: Song,
    description: string | undefined,
    fileUrl: string,
    key: Key | undefined
}

export interface DraftScore extends Omit<Partial<Score>, "song"> {
    song?: string
}

export interface Chords extends ApiEntity {
    song: Song,
    description: string | undefined,
    chords: string,
    key: Key | undefined
}

export interface DraftChords extends Omit<Partial<Chords>, "song"> {
    song?: string
}
export interface Song extends Identifiable {
    id: number,
    choir: Choir,
    title: string,
    composer: string,
    recordingUrl: string,
    scoreUrl: string,
    songbook: Songbook | undefined,
    songbookNumber: number,
    scores: Array<Score>,
    chords: Array<Chords>,
    slug: string,
    addedAt: Date,
    addedBy: User,
    categories: Array<Category>,
    setlistEntries: Array<SetlistEntry>,
    text: string,
    lastSetlist: Setlist | undefined
}

export interface Songbook extends Identifiable {
    id: number,
    title: string
}

export interface Setlist extends Identifiable {
    id: number | undefined,
    name: string,
    date: Date,
    choir: Choir,
    entries: Array<SetlistEntry>,
}

export interface SetlistEntry extends ApiEntity {
    id: string,
    setlist: Setlist,
    song: Song,
    number: number
}

export interface User extends Identifiable {
    id: number,
    choir: Choir,
    email: string,
    username: string,
    displayName: string,
    zitadelId: string
}

type Embedded<N extends string, O> = { 
    [K in N]: O
}

export interface WithEmbedded<N extends string, O> {
    _embedded: Embedded<N, O>
}