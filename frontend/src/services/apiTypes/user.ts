import { ApiEntity, fromDomain, Identifiable, toDomain } from ".";
import { Choir } from "./choir";
import { User as DomainUser } from "@/entities/user";

export interface User extends Identifiable, ApiEntity {
    id: number
    choir?: Choir
    email: string
    username: string
    displayName: string
}

export function toDomainUser(user: User): DomainUser {
    return toDomain(user);
}

export function fromDomainUser(user: DomainUser): User {
    return {
        id: user.id,
        email: user.email,
        username: user.username,
        displayName: user.displayName,
        _links: undefined
    };
}