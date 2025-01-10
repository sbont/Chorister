import { Entity } from "./entity";

export interface File extends Entity {
    id: number
    fileUrl?: string
}