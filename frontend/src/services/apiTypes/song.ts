import { ApiEntityIn, ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { Category, toDomainCategory } from "./category";
import { Choir } from "./choir";
import { Chords } from "./chords";
import { EventEntryIn } from "./event";
import { Score } from "./score";
import { User } from "./user";
import { Song as SongEntity } from "@/entities/song";
import { Uri } from "@/types";

interface Song extends Identifiable {
    id: number,
    title: string,
    composer: string,
    recordingUrl: string,
    scoreUrl: string,
    songbook: Songbook | undefined,
    songbookNumber: number,
    slug: string,
    addedAt: Date,
    text: string,
}

export interface SongIn extends Song, ApiEntityWith<ChordsLink & ScoresLink & EventEntriesLink & CategoriesLink> {
    choir: Choir,
    scores: Array<Score>,
    chords: Array<Chords>,
    addedBy: User,
    categories: Array<Category>,
    eventEntries: Array<EventEntryIn>,
    lastEvent: Event | undefined
}

export interface SongOut extends Song {
    choir: Uri | undefined,
    scores: Array<Uri> | undefined,
    chords: Array<Uri> | undefined,
    addedBy: Uri | undefined,
    categories: Array<Uri> | undefined,
    eventEntries: Array<Uri> | undefined,
    lastEvent: Uri | undefined
}

export interface ChordsLink extends WithAssociation {
    chords: Link
}

export interface ScoresLink extends WithAssociation {
    scores: Link
}

export interface CategoriesLink extends WithAssociation {
    categories: Link
}

export interface EventEntriesLink extends WithAssociation {
    eventEntries: Link
}

export interface Songbook extends Identifiable, ApiEntityIn {
    id: number,
    title: string
}

export function toDomainSong(apiSong: SongIn): SongEntity {
    return {...toDomain(apiSong),
        scores: apiSong._links?.scores ? { uri: untemplated(apiSong._links.scores) } : undefined,
        chords: apiSong._links?.chords ? { uri: untemplated(apiSong._links.chords) } : undefined,
        eventEntries: apiSong._links?.eventEntries ? { uri: untemplated(apiSong._links.eventEntries) } : undefined,
        categories: apiSong._links?.categories ? { uri: apiSong._links!.categories!.href, resolved: apiSong.categories?.map(toDomainCategory) } : undefined
    };
}

export function fromDomainSong(song: SongEntity): SongOut {
    return {... fromDomain(song),
        categories: undefined,
        choir: undefined,
        chords: undefined,
        addedBy: undefined,
        scores: undefined,
        eventEntries: undefined,
        lastEvent: undefined
    };
}
