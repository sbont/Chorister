import { ApiEntity, Identifiable } from "."
import { Choir } from "./choir"
import { User } from "./user"

export interface Invite extends Identifiable, ApiEntity {
    id: number
    email: string
    invitedBy: User
    token: string
    createdDate: Date
    acceptedDate: Date
    expired: Boolean
    choir: Choir
}