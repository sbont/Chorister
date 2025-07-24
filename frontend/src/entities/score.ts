import { Key } from "@/types/Key";
import { Entity, EntityRef } from "./entity";
import { Song } from "./song";
import { FileInfo } from "./fileInfo";

export interface Score extends Entity {
    song: EntityRef<Song>,
    description: string | undefined,
    fileUrl: string,
    key: Key | undefined,
    file?: FileInfo
}