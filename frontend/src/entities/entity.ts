import { Uri } from "@/types";

export interface Entity {
    id?: number;
    uri?: Uri;
}

export interface EntityRef<T extends Entity> {
    id: number | undefined;
    uri: Uri | undefined;
}

export interface EntityCollectionRef {
    uri: Uri
}

export function isNew(entity: Entity) {
    return entity.id != undefined;
}