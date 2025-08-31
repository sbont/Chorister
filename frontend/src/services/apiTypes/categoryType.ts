import { CategoryType as DomainCategoryType } from "@/entities/categoryType";
import { Uri } from "@/types";
import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { EntityRef } from "@/entities/entity";

export interface CategoryType extends Identifiable, ApiEntityWith<ChoirLink> {
    id: number,
    choir?: Uri,
    name: string,
}

export interface ChoirLink extends WithAssociation {
    choir: Link
}

export function toDomainCategoryType(categoryType: CategoryType): DomainCategoryType {
    return { ... toDomain(categoryType),
        choir: categoryType._links?.choir ? { uri: untemplated(categoryType._links.choir) } : undefined,
    };
}

export function fromDomainCategoryType(categoryType: DomainCategoryType): CategoryType {
    return { ... fromDomain(categoryType) };
}