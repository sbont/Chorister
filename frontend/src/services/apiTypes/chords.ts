import { Chords as DomainChords } from "@/entities/chords";
import { Uri } from "@/types";
import { Key } from "@/types/Key";
import { ApiEntityWith, fromDomain, Link, toDomain, untemplated, WithAssociation } from ".";

export interface Chords extends ApiEntityWith<SongLink> {
    id: number,
    song: Uri,
    description: string | undefined,
    chords: string,
    key: Key | undefined
}

export interface SongLink extends WithAssociation {
    song: Link
}

export function fromDomainChords(chords: DomainChords): Chords {
    return { ... fromDomain(chords),
        song: chords.song.uri!
    };
}

export function toDomainChords(chords: Chords): DomainChords {
    return {... toDomain(chords),
        song: { uri: untemplated(chords._links!.song) }
    };
}