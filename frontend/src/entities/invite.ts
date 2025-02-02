import { Choir } from "./choir"
import { Entity } from "./entity"
import { User } from "./user"

export interface Invite extends Entity {
    email?: string
    token: string
    choir: Choir
}