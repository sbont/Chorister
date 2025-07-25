import { Entity, EntityRef } from "./entity"
import { User } from "./user"

export interface Choir extends Entity {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager?: EntityRef<User>
}