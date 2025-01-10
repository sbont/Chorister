import { Uri } from "@/types";

export interface Entity {
    id?: number;
    uri?: Uri;
}

export interface EntityRef<T extends Entity> {
    id?: number;
    uri?: Uri;
    resolved?: T
}

export interface EntityCollectionRef<T extends Entity> {
    uri: Uri;
    resolved?: T[]
}

export function isNew(entity: Entity) {
    return entity.id != undefined;
}

export function toEntityRef<T extends Entity>(entity: T): EntityRef<T> {
    return { id: entity.id, uri: entity.uri, resolved: entity };
}