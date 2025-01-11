import { ApiEntityIn, Identifiable } from "./services/apiTypes";

export const isNew = (obj: any) => !(obj as ApiEntityIn)._links?.self.href && !(obj as Identifiable).id

export function isApiEntity(obj: any): obj is ApiEntityIn {
    return obj?._links?._self?._href !== undefined || obj?.id !== undefined
}