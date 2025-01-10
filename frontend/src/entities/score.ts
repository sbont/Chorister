import { Key } from "@/types/Key";
import { Entity, EntityRef } from "./entity";
import { Song } from "./song";
import { File } from "./file";

export interface Score extends Entity {
    song: EntityRef<Song>;
    description: string | undefined,
    fileUrl: string,
    key: Key | undefined,
    file: File
}