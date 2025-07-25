import { Choir } from "./choir";
import { Entity, EntityRef } from "./entity";

export interface User extends Entity {
    id: number,
    choir?: EntityRef<Choir>,
    email: string,
    username: string,
    displayName: string,
    zitadelId: string
}