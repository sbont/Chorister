import { Entity } from "@/entities/entity"
import { Uri } from "@/types"

export interface Identifiable {
    id: number | undefined
}

export type ApiEntityOut = Identifiable

export interface ApiEntityIn extends Identifiable {
    _links: ApiEntityLinks | undefined
}

export interface ApiEntityWith<W extends WithAssociation> {
    _links: ApiEntityLinks & W | undefined
}

export interface ApiEntityLinks {
    self: Link
}

export interface Link {
    href: Uri
    templated?: boolean
}

export interface WithAssociation { }

type Embedded<N extends string, O> = {
    [K in N]: O
}

export interface WithEmbedded<N extends string, O> {
    _embedded?: Embedded<N, O>
}

export function getSelfUri(apiEntity: ApiEntityIn): Uri | undefined {
    const link = apiEntity._links?.self;
    if (!link)
        return undefined;

    if (link.templated)
        return link.href.replace("{?projection}", "");

    return link.href;
}

export function untemplated(link: Link): Uri {
    if (link.templated)
        return link.href.replace("{?projection}", "");

    return link.href;
}

export function toDomain<Source extends ApiEntityIn, Target extends Entity>(apiEntity: Source) {
    return Object.assign({
        id: apiEntity.id,
        uri: apiEntity._links?.self ? untemplated(apiEntity._links?.self!) : undefined
    }, apiEntity) as Entity as Target;
}

export function fromDomain<Source extends Entity, Target extends Identifiable>(entity: Source) {
    return {
        ...entity,
        _links: undefined
    } as Entity as Target;
}