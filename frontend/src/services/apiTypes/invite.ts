import { ApiEntityIn, Identifiable } from "."
import { Choir } from "./choir"
import { User } from "./user"

export interface Invite extends Identifiable, ApiEntityIn {
    id: number
    email: string
    invitedBy: User
    token: string
    createdDate: Date
    acceptedDate: Date
    expired: Boolean
    choir: Choir
}