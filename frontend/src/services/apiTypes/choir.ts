import { Identifiable, ApiEntity, toDomain, fromDomain, WithAssociation, Link, ApiEntityWith, Uri } from "."
import { User } from "./user"
import { Choir as DomainChoir } from "@/entities/choir";

export interface Choir extends Identifiable, ApiEntityWith<ManagerLink> {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager: Uri | undefined
}

export interface ManagerLink extends WithAssociation {
    manager: Link
}

export function toDomainChoir(choir: Choir): DomainChoir {
    return { ... toDomain(choir),
        manager: { uri: choir._links!.manager.href }
    };
}

export function fromDomainChoir(choir: DomainChoir): Choir {
    return { ... fromDomain(choir),
        manager: choir.manager.uri
    };
}