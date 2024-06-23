import { ApiEntity, Identifiable } from "./types";

export const isNew = (obj: any) => !(obj as ApiEntity)._links?.self.href && !(obj as Identifiable).id

export function isApiEntity(obj: any): obj is ApiEntity {
    return obj?._links?._self?._href !== undefined || obj?.id !== undefined
}