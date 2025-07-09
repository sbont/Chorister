import { Uri } from "@/types";

export interface Entity {
    id?: number;
    uri?: Uri;
}

export interface EntityCollectionRef<T extends Entity> {
    uri?: Uri;
    resolved?: T[]
}

export function isNew(entity: Entity) {
    return entity.id == undefined && entity.uri == undefined;
}

export class EntityRef<T extends Entity> {
    private _uri?: Uri;
    id?: number;
    resolved?: T;

    constructor(uri: Uri)
    constructor(entity: T);
    constructor(entity?: T, uri?: Uri) {
        this.resolved = entity;
        this.id = entity?.id;
        this._uri = uri ?? entity?.uri;
    }
    
    get uri() {
        if (!this._uri)
            throw new Error(`Missing uri for object ${this.resolved ?? ""}`);
        
        return this._uri;        
    }
    
    set uri(uri: Uri) {
        this._uri = uri;
        if (this.resolved?.uri != uri)
            this.resolved = undefined;
    }
    
}