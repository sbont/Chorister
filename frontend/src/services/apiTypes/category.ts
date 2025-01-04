import { ApiEntity, ApiEntityWith, fromDomain, Identifiable, Link, toDomain, Uri, WithAssociation } from "."
import { Choir } from "./choir"
import { Category as DomainCategory } from "@/entities/category";

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
        choir: { uri: category._links!.choir.href }
    };
}

export function fromDomainCategory(category: DomainCategory): Category {
    return { ... fromDomain(category) };
}