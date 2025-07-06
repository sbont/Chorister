import { EntityRef } from "@/entities/entity";
import { ApiEntityIn, ApiEntityWith, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { Choir } from "./choir";
import { User as DomainUser } from "@/entities/user";

export interface User extends Identifiable, ApiEntityWith<ChoirLink> {
    id: number;
    choir?: Choir;
    email: string;
    username: string;
    displayName: string;
}

export interface ChoirLink extends WithAssociation {
    choir: Link
}

export function toDomainUser(user: User): DomainUser {
    const choir = user._links?.choir ? {
        uri: untemplated(user._links?.choir)
    } : new EntityRef<Choir>(user.choir!)
    return {
        ...toDomain(user),
        choir: choir
    };
}

export function fromDomainUser(user: DomainUser): User {
    return {
        id: user.id,
        email: user.email,
        username: user.username,
        displayName: user.displayName,
        _links: undefined,
    };
}
