import { Uri } from "@/types";

export interface Entity {
    id?: number;
    uri?: Uri;
}

export interface EntityCollectionRef<T extends Entity> {
    uri?: Uri;
    embedded?: T[]
}

export function isNew(entity: Entity) {
    return entity.id == undefined && entity.uri == undefined;
}

export class EntityRef<T extends Entity> {
    private _uri?: Uri;
    id?: number;
    embedded?: T;

    constructor(entity: T);
    constructor(entity: T | undefined, uri?: Uri) {
        this.embedded = entity;
        this.id = entity?.id;
        this._uri = uri ?? entity?.uri;
    }

    get uri() {
        if (!this._uri)
            throw new Error(`Missing uri for object ${this.embedded ?? ""}`);

        return this._uri;
    }

    set uri(uri: Uri) {
        this._uri = uri;
        if (this.embedded?.uri != uri)
            this.embedded = undefined;
    }

}