import { Key } from "@/types/Key";
import { ApiEntityWith, fromDomain, Link, toDomain, untemplated, WithAssociation } from ".";
import { Score as DomainScore } from "@/entities/score";
import { Uri } from "@/types";

export interface Score extends ApiEntityWith<SongLink> {
    id: number;
    song: Uri;
    description: string | undefined;
    fileUrl: string;
    key: Key | undefined;
    file: File;
}

export interface SongLink extends WithAssociation {
    song: Link
}

export function fromDomainScore(score: DomainScore): Score {
    return {... fromDomain(score),
        song: score.song.uri!
    };
}

export function toDomainScore(score: Score): DomainScore {
    return {... toDomain(score),
        song: { uri: untemplated(score._links!.song) }
    };
}