import { Key } from "./Key"

export interface Identifiable {
    id: number | undefined
}

export interface ApiEntity {
    _links: ApiEntityLinks | undefined
}

export interface ApiEntityWith<W extends WithAssociation> {
    _links: ApiEntityLinks & W | undefined
}

export interface ApiEntityLinks {
    self: Link
}

export interface Link {
    href: string
    templated?: boolean
}

export interface WithAssociation{};

export interface ChordsLink extends WithAssociation {
    chords: Link
}

export interface ScoresLink extends WithAssociation {
    scores: Link
}

export interface Choir extends Identifiable, ApiEntity {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager: User
}

export interface User extends Identifiable, ApiEntity {
    id: number,
    choir: Choir,
    email: string,
    username: string,
    displayName: string
}

export interface Invite extends Identifiable, ApiEntity {
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
    season: Array<Category>,
    liturgical: Array<Category>
}

export interface Category extends Identifiable, ApiEntity {
    id: number,
    choir: Choir,
    name: string,
    type: CategoryType
}

export enum CategoryType {
    Season = "SEASON",
    Liturgical = "LITURGICAL_MOMENT"
}

export interface Score extends ApiEntity {
    song: Song,
    description: string | undefined,
    fileUrl: string,
    key: Key | undefined,
    file: File
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
export interface Song extends Identifiable, ApiEntityWith<ChordsLink & ScoresLink> {
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

export interface Songbook extends Identifiable, ApiEntity {
    id: number,
    title: string
}

export interface Setlist extends Identifiable, ApiEntity {
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

export interface User extends Identifiable, ApiEntity {
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

export interface File {
    id: number,
    fileUrl?: string
}

export interface WithEmbedded<N extends string, O> {
    _embedded: Embedded<N, O>
}

export interface UploadReturnEnvelope {
    fileId: number,
    uploadUrl: string,
}
