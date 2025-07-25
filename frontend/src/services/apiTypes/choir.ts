import { Choir as DomainChoir } from "@/entities/choir";
import { User as DomainUser } from "@/entities/user";
import { Uri } from "@/types";
import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, WithAssociation } from ".";
import { toDomainUser, User } from "./user";
import { EntityRef } from "@/entities/entity";

export interface Choir extends Identifiable, ApiEntityWith<ManagerLink> {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager?: Uri | User
}

export interface ManagerLink extends WithAssociation {
    manager: Link
}

export function toDomainChoir(choir: Choir): DomainChoir {
    const manager = choir.manager ?
        ((choir.manager as User).id ?
            new EntityRef<DomainUser>(toDomainUser(choir.manager as User)) :
            { uri: choir.manager as Uri }) :
        undefined;
    return {
        ...toDomain(choir),
        manager
    };
}

export function fromDomainChoir(choir: DomainChoir): Choir {
    return {
        ...fromDomain(choir),
        manager: choir.manager?.uri
    };
}