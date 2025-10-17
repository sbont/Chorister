import { Category as DomainCategory } from "@/entities/category";
import { Uri } from "@/types";
import { ApiEntityWith, fromDomain, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";
import { EntityRef } from "@/entities/entity";
import { CategoryType } from "./categoryType";

export interface Category extends Identifiable, ApiEntityWith<ChoirLink & CategoryTypeLink> {
    id: number,
    choir?: Uri,
    name: string,
    categoryType?: Uri
}

export interface ChoirLink extends WithAssociation {
    choir: Link
}

export interface CategoryTypeLink extends WithAssociation {
    categoryType: Link
}

export function toDomainCategory(category: Category): DomainCategory {
    return {
        ...toDomain(category),
        choir: category._links?.choir ? { uri: untemplated(category._links.choir) } : undefined,
        categoryType: category._links?.categoryType ? { uri: untemplated(category._links.categoryType) } : undefined
    };
}

export function fromDomainCategory(category: DomainCategory): Category {
    console.log(category);
    console.log({
        ...fromDomain(category),
        categoryType: category.categoryType?.uri
    });
    
    
    return {
        ...fromDomain(category),
        categoryType: category.categoryType?.uri
    };
}