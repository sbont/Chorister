import { ApiEntityIn, ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { Category } from "./category";
import { Choir } from "./choir";
import { Chords } from "./chords";
import { EventEntryGet } from "./event";
import { Score } from "./score";
import { User } from "./user";
import { Song as SongEntity } from "@/entities/song";

export interface Song extends ApiEntityWith<ChordsLink & ScoresLink & EventEntriesLink> {
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
    eventEntries: Array<EventEntryGet>,
    text: string,
    lastEvent: Event | undefined
}

export interface ChordsLink extends WithAssociation {
    chords: Link
}

export interface ScoresLink extends WithAssociation {
    scores: Link
}

export interface EventEntriesLink extends WithAssociation {
    eventEntries: Link
}

export interface Songbook extends Identifiable, ApiEntityIn {
    id: number,
    title: string
}

export function toDomainSong(apiSong: Song): SongEntity {
    return {...toDomain(apiSong),
        scores: apiSong._links?.scores ? { uri: untemplated(apiSong._links.scores) } : undefined,
        chords: apiSong._links?.chords ? { uri: untemplated(apiSong._links.chords) } : undefined,
        eventEntries: apiSong._links?.eventEntries ? { uri: untemplated(apiSong._links.eventEntries) } : undefined,
    };
}

export function fromDomainSong(song: SongEntity): Song {
    return {... fromDomain(song),
        
    };
}
