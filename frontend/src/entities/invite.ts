import { Choir } from "./choir"
import { Entity } from "./entity"
import { User } from "./user"

export interface Invite extends Entity {
    id: number
    email: string
    invitedBy: User
    token: string
    createdDate: Date
    acceptedDate: Date
    expired: Boolean
    choir: Choir
}