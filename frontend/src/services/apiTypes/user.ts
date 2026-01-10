import { Choir as DomainChoir } from "@/entities/choir";
import { EntityRef } from "@/entities/entity";
import { User as DomainUser } from "@/entities/user";
import { ApiEntityWith, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { Choir, toDomainChoir } from "./choir";
import { Role } from "@/types/role";

export interface User extends Identifiable, ApiEntityWith<ChoirLink> {
    id: number;
    choir?: Choir;
    email: string;
    username: string;
    firstName: string;
    lastName: string | null;
    roles?: Role[]
}


export interface ChoirLink extends WithAssociation {
    choir: Link
}

export function toDomainUser(user: User): DomainUser {
    const choir = user._links?.choir ? {
        uri: untemplated(user._links?.choir)
    } : (user.choir ? new EntityRef<DomainChoir>(toDomainChoir(user.choir)) : undefined);
    return {
        ...toDomain(user),
        lastName: user.lastName ?? undefined,
        choir: choir,
    };
}

export function fromDomainUser(user: DomainUser): User {
    return {
        id: user.id,
        email: user.email,
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName ?? null,
        _links: undefined,
    };
}
