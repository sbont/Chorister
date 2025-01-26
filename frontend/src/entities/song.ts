import { Category } from "./category";
import { Choir } from "./choir";
import { Chords } from "./chords";
import { Entity, EntityCollectionRef, EntityRef } from "./entity";
import { Event, EventEntry } from "./event";
import { Score } from "./score";
import { User } from "./user";

export interface Song extends Entity {
    id: number
    choir: EntityRef<Choir>
    title: string
    composer: string
    recordingUrl: string
    scoreUrl: string
    songbook: Songbook | undefined
    songbookNumber: number
    scores?: EntityCollectionRef<Score>
    chords?: EntityCollectionRef<Chords>
    slug: string
    addedAt: Date
    addedBy: EntityRef<User>
    categories: EntityCollectionRef<Category>
    eventEntries?: EntityCollectionRef<EventEntry>
    text: string
    lastEvent: Event | undefined
}

export interface Songbook {
    id: number
    title: string
}