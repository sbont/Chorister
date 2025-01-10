import { Key } from "@/types/Key";
import { Entity, EntityRef } from "./entity";
import { Song } from "./song";

export interface Chords extends Entity {
    song: EntityRef<Song>,
    description: string | undefined,
    chords: string,
    key: Key | undefined
}