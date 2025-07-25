import { Choir } from "./choir"
import { Entity } from "./entity"

export interface Invite extends Entity {
    email?: string
    token: string
    choir: Choir
}