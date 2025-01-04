import { EntityRef } from "@/entities/entity";
import { ApiEntity, ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, Uri, WithAssociation, WithEmbedded } from ".";
import { Choir } from "./choir";
import { Song } from "./song";
import { Event as DomainEvent, EventEntry as DomainEventEntry } from "@/entities/event";


export interface Event extends Identifiable, ApiEntity {
    id: number | undefined,
    name: string,
    date: Date,
    choir: Uri,
}

export type EventGet = Event & WithEmbedded<"entries", Array<EventEntry>>

export interface EventPost extends Event {
    entries: Array<Uri>
}

export interface EventEntry extends ApiEntityWith<SongLink & EventLink> {
    id: number,
    event: Uri,
    song: Uri,
    label: string,
    sequence: number
}

export interface SongLink extends WithAssociation {
    song: Link
}

export interface EventLink extends WithAssociation {
    event: Link
}

export function toDomainEvent(apiEvent: EventGet): DomainEvent {
    return {
        ...toDomain(apiEvent),
        entries: apiEvent._embedded.entries.map(toDomainEventEntry)
    };
}

export function fromDomainEvent(event: DomainEvent): EventPost {
    return {
        ...fromDomain(event),
        entries: event.entries.map(e => e.uri!)
    };
}

export function toDomainEventEntry(apiEntry: EventEntry): DomainEventEntry {
    return {
        ...toDomain(apiEntry),
        event: { uri: untemplated(apiEntry._links!.event) },
        song: { uri: untemplated(apiEntry._links!.song) },
    };
}

export function fromDomainEventEntry(entry: DomainEventEntry): EventEntry {
    return {
        ...fromDomainEventEntry(entry),
        event: entry.event.uri!,
        song: entry.song.uri!
    };
}
