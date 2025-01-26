import { Category as DomainCategory } from "@/entities/category";
import { Uri } from "@/types";
import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, WithAssociation } from ".";
import { EntityRef } from "@/entities/entity";

export interface Category extends Identifiable, ApiEntityWith<ChoirLink> {
    id: number,
    choir?: Uri,
    name: string,
    type: CategoryType
}

export enum CategoryType {
    Season = "SEASON",
    Liturgical = "LITURGICAL_MOMENT"
}

export interface ChoirLink extends WithAssociation {
    choir: Link
}

export function toDomainCategory(category: Category): DomainCategory {
    return { ... toDomain(category),
        choir: category._links?.choir ? new EntityRef(category._links.choir.href) : undefined
    };
}

export function fromDomainCategory(category: DomainCategory): Category {
    return { ... fromDomain(category) };
}