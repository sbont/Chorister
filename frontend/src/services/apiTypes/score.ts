import { Score as DomainScore } from "@/entities/score";
import { Uri } from "@/types";
import { Key } from "@/types/Key";
import { ApiEntityWith, fromDomain, Link, toDomain, untemplated, WithAssociation } from ".";
import { FileInfo } from "./fileInfo";

export interface Score extends ApiEntityWith<SongLink> {
    id: number;
    song: Uri;
    description: string | undefined;
    fileUrl: string;
    key: Key | undefined;
    file: FileInfo;
}

export interface SongLink extends WithAssociation {
    song: Link
}

export function fromDomainScore(score: DomainScore): Score {
    return {
        ...fromDomain(score),
        song: score.song.uri!
    };
}

export function toDomainScore(fileUrl: (id: number) => string): (score: Score) => DomainScore {
    return (score: Score) => {
        return {
            ...toDomain(score),
            song: { uri: untemplated(score._links!.song) },
            file: score.file ? {
                id: score.file.id,
                fileInfoUri: fileUrl(score.file.id)
            } : undefined
        }
    }
};