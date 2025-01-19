import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, WithAssociation, WithEmbedded } from ".";
import { Song, toDomainSong } from "./song";
import { Event as DomainEvent, EventEntry as DomainEventEntry } from "@/entities/event";
import { Uri } from "@/types";

export type EventGet = Event & WithEmbedded<"entries", Array<EventEntryGet>> & ApiEntityWith<EventEntriesLink>;

export interface EventPost extends Event {
    entries?: Array<Uri>
}

export interface Event extends Identifiable {
    name: string,
    date: Date,
    choir?: Uri,
}

export interface EventEntriesLink extends WithAssociation {
    entries: Link
}

export interface EventEntryGet extends EventEntry, ApiEntityWith<SongLink & EventLink> {
    event: Event,
    song?: Song,
}

export interface EventEntryPost extends EventEntry {
    event: Uri,
    song: Uri,
}

export interface EventEntry extends Identifiable {
    id: number,
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
        entries: {
            uri: untemplated(apiEvent._links?.entries!),
            resolved: apiEvent._embedded.entries.map(toDomainEventEntry)
        }
    };
}

export function fromDomainEvent(event: DomainEvent): EventPost {
    return {
        ...fromDomain(event),
        entries: event.entries.resolved?.map(e => e.uri!)
    };
}

export function toDomainEventEntry(apiEntry: EventEntryGet): DomainEventEntry {
    return {
        ...toDomain(apiEntry),
        event: { 
            uri: untemplated(apiEntry._links!.event)
        },
        song: { 
            uri: untemplated(apiEntry._links!.song),
            resolved: apiEntry.song ? toDomainSong(apiEntry.song) : undefined
        },
    };
}

export function fromDomainEventEntry(entry: DomainEventEntry): EventEntryPost {
    return {
        ...fromDomain(entry),
        event: entry.event.uri,
        song: entry.song.uri
    };
}
