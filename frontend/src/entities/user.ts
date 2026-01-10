import { Role } from "@/types/role";
import { Choir } from "./choir";
import { Entity, EntityRef } from "./entity";

export interface User extends Entity {
    id: number,
    choir?: EntityRef<Choir>,
    email: string,
    username: string,
    firstName: string,
    lastName?: string,
    zitadelId: string,
    roles: Role[]
}