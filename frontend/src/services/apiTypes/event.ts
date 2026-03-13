import { notNullOrUndefined } from "@/utils";
import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, toEntityRef, untemplated, WithAssociation, WithEmbedded } from ".";
import { SongIn, toDomainSong } from "./song";
import { Event as DomainEvent, EventEntry as DomainEventEntry } from "@/entities/event";
import { Uri } from "@/types";

interface Event extends Identifiable {
    name: string,
    date: Date,
    choir?: Uri,
}

export type EventIn = Event & WithEmbedded<"entries", Array<EventEntryIn>> & ApiEntityWith<EventEntriesLink>;

export interface EventOut extends Event {
    entries?: Array<Uri>
}

export interface EventEntriesLink extends WithAssociation {
    entries: Link
}

export interface EventEntry extends Identifiable {
    id: number,
    label: string,
    sequence: number,
    songTitle: string
}

export interface EventEntryIn extends EventEntry, ApiEntityWith<SongLink & EventLink>, WithEmbedded<"song", SongIn> {
    event: Event,
    song?: SongIn,
}

export interface EventEntryOut extends EventEntry {
    event?: Uri,
    song?: Uri,
    label: string
}

export interface SongLink extends WithAssociation {
    song: Link
}

export interface EventLink extends WithAssociation {
    event: Link
}

export function toDomainEvent(apiEvent: EventIn): DomainEvent {
    return {
        ...toDomain(apiEvent),
        entries: {
            uri: untemplated(apiEvent._links?.entries),
            embedded: apiEvent._embedded?.entries.map(toDomainEventEntry)
        }
    };
}

export function fromDomainEvent(event: DomainEvent): EventOut {
    return {
        ...fromDomain(event),
        entries: event.entries?.embedded?.map(e => e.uri).filter(notNullOrUndefined)
    };
}

export function toDomainEventEntry(apiEntry: EventEntryIn): DomainEventEntry {
    const song = toEntityRef(apiEntry._links?.song, apiEntry.song, toDomainSong);
    return {
        ...toDomain(apiEntry),
        song,
        event: toEntityRef(apiEntry._links?.event)
    };
}

export function fromDomainEventEntry(entry: DomainEventEntry): EventEntryOut {
    return {
        ...fromDomain(entry),
        event: entry.event?.uri,
        song: entry.song?.uri
    };
}
