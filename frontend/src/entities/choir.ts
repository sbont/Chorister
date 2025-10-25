import { Entity, EntityRef } from "./entity"
import { LiturgicalBook } from "./liturgicalBook"
import { Rite } from "./rite"
import { User } from "./user"

export interface Choir extends Entity {
    id: number
    name: string
    type: string
    inviteToken?: string
    manager?: EntityRef<User>
    rite?: EntityRef<Rite>
    liturgicalBook?: EntityRef<LiturgicalBook>
}