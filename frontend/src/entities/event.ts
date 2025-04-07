import { Entity, EntityCollectionRef, EntityRef } from "./entity";
import { Song } from "./song";

export interface Event extends Entity {
    id?: number,
    name: string,
    date: Date,
    entries: EntityCollectionRef<EventEntry>
}

export interface EventEntry extends Entity {
    id?: number,
    event: EntityRef<Event>,
    song?: EntityRef<Song>,
    label?: string,
    sequence: number
}